import logging
import os
import re
import requests
import difflib
from datetime import datetime, timedelta

# --- Google Cloud Vision ---
try:
    from google.cloud import vision
except ImportError:
    vision = None

# --- For OpenAI GPT usage ---
try:
    import openai
except ImportError:
    openai = None

logger = logging.getLogger(__name__)

class FactChecker:
    def __init__(self, db):
        """Initialize the fact checker with database connection."""
        self.db = db

        # 1) Google Fact Check API
        self.factcheck_api_key = os.getenv('GOOGLE_FACTCHECK_API_KEY')  # no default
        self.factcheck_url = 'https://factchecktools.googleapis.com/v1alpha1/claims:search'
        
        # 2) OpenAI / GPT (if needed)
        self.openai_api_key = os.getenv('OPENAI_API_KEY')
        if openai and self.openai_api_key:
            openai.api_key = self.openai_api_key
            logger.info("✅ OpenAI API key loaded successfully.")
        else:
            logger.warning("⚠️ OpenAI library missing or OPENAI_API_KEY not set. GPT analysis may fail.")
        
        # 3) Gemini (Google Generative AI)
        self.gemini_api_key = os.getenv('GEMINI_API_KEY')
        self.gemini_client = None
        try:
            import google.generativeai as genai
            if self.gemini_api_key:
                genai.configure(api_key=self.gemini_api_key)
                self.gemini_client = genai.GenerativeModel("gemini-2.0-flash")
                logger.info("✅ Gemini client initialized successfully.")
            else:
                logger.warning("⚠️ GEMINI_API_KEY not found. Gemini analysis will be disabled.")
        except ImportError:
            logger.warning("⚠️ google-generativeai not installed. Gemini analysis unavailable.")
        except Exception as e:
            logger.error(f"❌ Failed to initialize Gemini client: {e}")
        
        # 4) Google Cloud Vision
        self.vision_client = None
        if vision:
            try:
                credentials_path = os.getenv("GOOGLE_APPLICATION_CREDENTIALS")
                if not credentials_path or not os.path.exists(credentials_path):
                    logger.warning("⚠️ GOOGLE_APPLICATION_CREDENTIALS not set or file not found. Vision OCR may fail.")
                else:
                    from google.oauth2 import service_account
                    creds = service_account.Credentials.from_service_account_file(credentials_path)
                    self.vision_client = vision.ImageAnnotatorClient(credentials=creds)
                    logger.info("✅ Google Vision client initialized successfully.")
            except Exception as e:
                logger.error(f"❌ Failed to initialize Google Vision client: {e}")
        else:
            logger.warning("⚠️ google-cloud-vision is not installed. OCR will be disabled.")
        
        # 5) Caching
        self.cache = {}
        self.cache_expiry = 3600  # seconds

    # ---------------------------
    # Internal helper functions
    # ---------------------------
    def _cache_result(self, key, result):
        self.cache[key] = {'timestamp': datetime.now(), 'result': result}

    def _standardize_record(self, record, similarity):
        """Convert a known misinformation record to a standardized result dict."""
        return {
            'is_misinformation': True,
            'confidence': similarity * 100,  # as percentage
            'category': record['category'],
            'fact_check': record['fact_check'],
            'gemini_analysis': None
        }

    def _match_known_misinformation(self, message):
        """Fuzzy-match the input message against known misinformation from the DB."""
        known = self.db.get_known_misinformation()
        best_match = None
        best_ratio = 0.0
        for record in known:
            ratio = difflib.SequenceMatcher(None, message.lower(), record['pattern'].lower()).ratio()
            if ratio > best_ratio:
                best_ratio = ratio
                best_match = record
        if best_match and best_ratio >= 0.7:
            logger.info(f"Fuzzy match found: similarity {best_ratio:.2f} for pattern: {best_match['pattern']}")
            return self._standardize_record(best_match, best_ratio)
        return None

    # ---------------------------
    # Public functions
    # ---------------------------
    def check_message(self, message, force_api=False):
        """
        Check a message for misinformation.

        If force_api is False (the default), first attempt fuzzy matching against the known misinformation
        in the DB. If a match is found, return that standardized result.
        If no match is found (and force_api is False), return a default (clean) result.
        If force_api is True (e.g., for /scan or image scanning), perform an external FactCheck API call,
        then override the result with Gemini analysis (if available), mark the message as misinformation,
        and store the standardized result in the DB.
        
        Returns a dict:
        {
            'is_misinformation': bool,
            'confidence': float,
            'category': str,
            'fact_check': str,
            'gemini_analysis': str
        }
        """
        result = {
            'is_misinformation': False,
            'confidence': 0.0,
            'category': None,
            'fact_check': None,
            'gemini_analysis': None
        }
        
        # Short messages are ignored
        if len(message.split()) < 5:
            return result

        cache_key = message.lower()
        # Use cache if available and not forcing an API call
        if not force_api and cache_key in self.cache:
            entry = self.cache[cache_key]
            if datetime.now() - entry['timestamp'] < timedelta(seconds=self.cache_expiry):
                logger.info("Using cached fact check result for this message.")
                return entry['result']
        
        # For normal messages (force_api==False), try fuzzy matching only.
        if not force_api:
            db_match = self._match_known_misinformation(message)
            if db_match:
                self._cache_result(cache_key, db_match)
                return db_match
            else:
                # No DB match—do not call external API; simply return the default (clean) result.
                self._cache_result(cache_key, result)
                return result

        # If force_api is True, then proceed with external API call.
        logger.info("Performing external API scan...")
        try:
            params = {
                'key': self.factcheck_api_key,
                'query': message,
                'languageCode': 'en-US'
            }
            resp = requests.get(self.factcheck_url, params=params)
            if resp.status_code == 200:
                data = resp.json()
                if data.get('claims'):
                    claim = data['claims'][0]
                    claim_rating = self._extract_rating(claim)
                    is_misinfo, confidence = self._evaluate_rating(claim_rating)
                    result['is_misinformation'] = is_misinfo
                    result['confidence'] = confidence
                    result['category'] = self._determine_category(claim)
                    result['fact_check'] = self._format_fact_check(claim)
                else:
                    result = self._basic_analysis(message)
            else:
                logger.error(f"FactCheck API error {resp.status_code}: {resp.text}")
                result = self._basic_analysis(message)
        except Exception as e:
            logger.error(f"Error querying FactCheck API: {e}")
            result = self._basic_analysis(message)

        # For forced scans, override result with Gemini analysis (if available)
        gemini_result = self.analyze_with_gpt(message)
        if gemini_result and gemini_result != "GPT analysis not available.":
            result['gemini_analysis'] = gemini_result
            result['is_misinformation'] = True
            result['confidence'] = max(result['confidence'], 60.0)
            result['category'] = "Gemini-Flagged Content"
            result['fact_check'] = f"Gemini Analysis: {gemini_result}"
        
        self._cache_result(cache_key, result)
        
        # Save the external API result into the DB for future fuzzy matching.
        try:
            store_id = self.db.store_fact_check(message, result)
            logger.info(f"Stored fact check result in DB with ID {store_id}.")
        except Exception as e:
            logger.error(f"Error storing fact check result in DB: {e}")
        
        logger.info(f"External API scan completed: {result['is_misinformation']} with confidence {result['confidence']}%.")
        return result


    def analyze_with_gpt(self, text):
        """
        Analyze text using Gemini (Google Generative AI).
        Returns a string with the analysis.
        """
        if not self.gemini_api_key or not self.gemini_client:
            logger.warning("Gemini API not configured. Skipping GPT analysis.")
            return "GPT analysis not available."
        try:
            prompt = (
                "Analyze the following text for potential misinformation or false claims.\n\n"
                f"{text}\n\n"
                "Return your analysis in 2-3 sentences."
            )
            resp = self.gemini_client.generate_content(prompt)
            if resp and resp.text:
                return resp.text.strip()
            logger.error("Gemini returned no text.")
            return "Could not perform Gemini analysis."
        except Exception as e:
            logger.error(f"Error analyzing text with Gemini: {e}")
            return "Error performing Gemini analysis. Please verify with reliable sources."

    def check_deepfake(self, image_url):
        """
        Check if an image is potentially a deepfake using the SightEngine API.
        """
        api_user = os.getenv('SIGHTENGINE_API_USER')
        api_secret = os.getenv('SIGHTENGINE_API_SECRET')
        if not api_user or not api_secret:
            logger.error("SightEngine API credentials not found.")
            return {
                'is_deepfake': False,
                'confidence': 0.0,
                'analysis': 'SightEngine API credentials not configured.'
            }
        try:
            params = {
                'url': image_url,
                'models': 'genai,scam,deepfake,face-attributes',
                'api_user': api_user,
                'api_secret': api_secret
            }
            se_response = requests.get('https://api.sightengine.com/1.0/check.json', params=params)
            if se_response.status_code == 200:
                return self._process_sightengine_response(se_response.json())
            else:
                logger.error(f"SightEngine error {se_response.status_code}: {se_response.text}")
                return {
                    'is_deepfake': False,
                    'confidence': 0.0,
                    'analysis': f"SightEngine request error: {se_response.text}"
                }
        except Exception as e:
            logger.error(f"Error in deepfake detection: {e}")
            return {
                'is_deepfake': False,
                'confidence': 0.0,
                'analysis': f"Error during deepfake analysis: {str(e)}"
            }

    def extract_text_from_image(self, image_path=None, image_url=None, image_content=None):
        """
        Extract text from an image using Google Vision (OCR).
        """
        if not self.vision_client:
            logger.error("Google Vision client is not initialized.")
            return None
        from google.cloud import vision as gcv
        try:
            image = gcv.Image()
            if image_content:
                image.content = image_content
            elif image_path:
                with open(image_path, 'rb') as f:
                    image.content = f.read()
            elif image_url:
                resp = requests.get(image_url)
                if resp.status_code == 200:
                    image.content = resp.content
                else:
                    logger.error(f"Failed to download image: {resp.status_code}")
                    return None
            else:
                logger.error("No image source provided to extract_text_from_image()")
                return None
            response = self.vision_client.text_detection(image=image)
            texts = response.text_annotations
            if texts:
                extracted_text = texts[0].description
                logger.info(f"Extracted {len(extracted_text)} characters from image.")
                return extracted_text
            else:
                logger.info("No text found in the image.")
                return ""
        except Exception as e:
            logger.error(f"Error extracting text from image: {e}")
            return None

    def _process_sightengine_response(self, se_data):
        """
        Internal method to interpret the SightEngine JSON.
        """
        reasons = []
        is_deepfake = False
        confidence_score = 0.0
        if se_data.get('status') != 'success':
            msg = f"SightEngine status: {se_data.get('error', {}).get('message', 'Unknown error')}"
            reasons.append(msg)
            return {
                'is_deepfake': False,
                'confidence': 0.0,
                'analysis': "\n".join(reasons)
            }
        if 'scam' in se_data and 'prob' in se_data['scam']:
            scam_score = se_data['scam']['prob'] * 100
            if scam_score > 15:
                is_deepfake = True
                confidence_score = max(confidence_score, scam_score)
                reasons.append(f"Potential scam content detected with {scam_score:.2f}% confidence")
        if 'type' in se_data:
            if 'ai_generated' in se_data['type']:
                ai_score = se_data['type']['ai_generated'] * 100
                if ai_score > 15:
                    is_deepfake = True
                    confidence_score = max(confidence_score, ai_score)
                    reasons.append(f"AI-generated image detected with {ai_score:.2f}% confidence")
            if 'deepfake' in se_data['type']:
                df_score = se_data['type']['deepfake'] * 100
                if df_score > 15:
                    is_deepfake = True
                    confidence_score = max(confidence_score, df_score)
                    reasons.append(f"Deepfake detected with {df_score:.2f}% confidence")
        if 'faces' in se_data and se_data['faces']:
            for idx, face in enumerate(se_data['faces']):
                if 'synthetic' in face and face['synthetic'] > 0.2:
                    is_deepfake = True
                    face_conf = face['synthetic'] * 100
                    confidence_score = max(confidence_score, face_conf)
                    reasons.append(f"Face #{idx+1} appears AI-generated with {face_conf:.2f}% confidence")
        if not reasons:
            reasons.append("No suspicious patterns detected by SightEngine.")
        analysis_msg = "\n- ".join(["Analysis results:"] + reasons)
        return {
            'is_deepfake': is_deepfake,
            'confidence': confidence_score,
            'analysis': analysis_msg
        }

    def _basic_analysis(self, message):
        """
        Fallback analysis if no FactCheck data is available.
        """
        result = {
            'is_misinformation': False,
            'confidence': 0.0,
            'category': None,
            'fact_check': None
        }
        exaggeration_indicators = [
            'all', 'every', 'always', 'never', 'everyone', 'nobody',
            'definitely', 'absolutely', 'undeniably', 'proven', 'guaranteed'
        ]
        conspiracy_indicators = [
            'conspiracy', 'cover up', 'cover-up', 'truth', 'hidden', 'secret',
            "they don't want you to know", 'government is hiding', 'real truth',
            "what they won't tell you", "mainstream media won't report"
        ]
        exaggeration_count = sum(1 for word in message.lower().split() if word in exaggeration_indicators)
        conspiracy_count = sum(1 for phrase in conspiracy_indicators if phrase in message.lower())
        confidence = 0.0
        if exaggeration_count > 2:
            confidence += 30.0
        if conspiracy_count > 0:
            confidence += 40.0
        has_url = bool(re.search(r'https?://\S+', message))
        has_citation = bool(re.search(r'\([^)]*\d{4}[^)]*\)', message))
        if not (has_url or has_citation) and len(message.split()) > 30:
            confidence += 20.0
        if confidence >= 60.0:
            result['is_misinformation'] = True
            result['confidence'] = confidence
            if conspiracy_count > 0:
                result['category'] = 'Conspiracy Theory'
                result['fact_check'] = ("This message contains language commonly associated with conspiracy theories. "
                                        "Consider checking reliable sources.")
            else:
                result['category'] = 'Unverified Claim'
                result['fact_check'] = ("This message contains exaggerated claims without proper sources. "
                                        "Consider verifying with reliable sources.")
        return result

    def _extract_rating(self, claim):
        """Extract rating from the first claimReview if available."""
        if 'claimReview' in claim:
            for r in claim['claimReview']:
                if 'textualRating' in r:
                    return r['textualRating']
        return None

    def _evaluate_rating(self, rating):
        """Evaluate if the rating means misinformation."""
        if not rating:
            return False, 0.0
        rating_lower = rating.lower()
        false_patterns = [
            'false', 'fake', 'incorrect', 'misleading', 'pants on fire',
            'mostly false', 'untrue', 'inaccurate', 'wrong', 'fiction',
            'not true', 'no evidence', 'unsupported'
        ]
        partially_false_patterns = [
            'mixture', 'partly', 'half', 'mixed', 'ambiguous',
            'needs context', 'not the full story', 'partially'
        ]
        for pattern in false_patterns:
            if pattern in rating_lower:
                if 'pants on fire' in rating_lower or 'completely false' in rating_lower:
                    return True, 95.0
                elif 'false' in rating_lower or 'fake' in rating_lower:
                    return True, 90.0
                else:
                    return True, 80.0
        for pattern in partially_false_patterns:
            if pattern in rating_lower:
                return True, 60.0
        return False, 0.0

    def _determine_category(self, claim):
        """Determine a category from the claim if possible."""
        category = "Unverified Claim"
        if 'claimReview' in claim:
            for r in claim['claimReview']:
                if 'title' in r:
                    title_lower = r['title'].lower()
                    if any(k in title_lower for k in ['conspiracy', 'theory']):
                        return "Conspiracy Theory"
                    elif any(k in title_lower for k in ['hoax', 'fake']):
                        return "Hoax"
                    elif any(k in title_lower for k in ['misleading', 'misrepresentation']):
                        return "Misleading Content"
                    elif any(k in title_lower for k in ['satire', 'parody']):
                        return "Satire/Parody"
        return category

    def _format_fact_check(self, claim):
        """Format a textual fact check response."""
        if 'claimReview' in claim and claim['claimReview']:
            review = claim['claimReview'][0]
            publisher_name = review.get('publisher', {}).get('name', '')
            rating = review.get('textualRating', '')
            url = review.get('url', '')
            parts = []
            if publisher_name:
                parts.append(f"According to {publisher_name}: ")
            if rating:
                parts.append(f"{rating}. ")
            if url:
                parts.append(f"More details: {url}")
            combined = "".join(parts)
            return combined.strip() if combined else "No specific fact check info. Please verify with reliable sources."
        return "No specific fact check information available. Please verify with reliable sources."

    def store_fact_check(self, text, result):
        """
        Store the external API fact check result in the KnownMisinformation table.
        Uses the provided text as the pattern and the standardized result for fact_check and category.
        The source is marked as 'External API'.
        Returns the ID of the new record.
        """
        session = self.Session()
        new_record = KnownMisinformation(
            pattern=text,
            fact_check=result.get('fact_check') or "No fact check available",
            category=result.get('category') or "Unverified",
            source="External API"
        )
        session.add(new_record)
        session.commit()
        record_id = new_record.id
        session.close()
        return record_id
