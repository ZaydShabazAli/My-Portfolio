import os
import time

# -------------------------------------------------------------------
# Force the local timezone to UTC before importing telegram/ext or APScheduler
# -------------------------------------------------------------------
os.environ["TZ"] = "UTC"
base_dir = os.path.dirname(__file__)

# Construct a relative file path
file_path = os.path.join(base_dir, "google-cloud-credentials.json")
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = file_path

import logging
import re
from dotenv import load_dotenv
from telegram import Update, ChatPermissions, InlineKeyboardButton, InlineKeyboardMarkup, Poll
from telegram.constants import ParseMode
from telegram.ext import (
    Application,
    CommandHandler,
    MessageHandler,
    CallbackQueryHandler,
    PollHandler,
    filters,
    CallbackContext,
)
from telegram.error import TelegramError

# Import custom modules
from modules.fact_checker import FactChecker
from modules.database import Database
from modules.user_manager import UserManager
from modules.report_generator import ReportGenerator
from google.auth import default

# Set up logging
logging.basicConfig(
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    level=logging.INFO
)
logger = logging.getLogger(__name__)

# Check credentials
credentials, project_id = default()
print(f"✅ Google Cloud authenticated with project: {project_id}")

# Load environment variables
load_dotenv()
TOKEN = os.getenv('TELEGRAM_BOT_TOKEN')
print(f"Token is: {TOKEN}")

class MisinfoBot:
    def __init__(self):
        """Initialize the bot with necessary components"""
        self.application = (
            Application.builder()
            .token(TOKEN)
            .build()
        )
        
        # Initialize components
        self.db = Database()
        self.fact_checker = FactChecker(self.db)
        self.user_manager = UserManager(self.db)
        self.report_generator = ReportGenerator(self.db)

        # Dictionary to store message statistics per chat
        self.message_stats = {}  # {chat_id: {"total": 0, "misinfo": 0}}
        
        # Register handlers
        self._register_handlers()
        
    def _register_handlers(self):
        """Register all command and message handlers"""
        # Command handlers
        self.application.add_handler(CommandHandler("start", self.start))
        self.application.add_handler(CommandHandler("help", self.help))
        self.application.add_handler(CommandHandler("report", self.generate_report))
        self.application.add_handler(CommandHandler("mute", self.mute_user))
        self.application.add_handler(CommandHandler("unmute", self.unmute_user))
        self.application.add_handler(CommandHandler("kick", self.kick_user))
        self.application.add_handler(CommandHandler("ban", self.ban_user))
        self.application.add_handler(CommandHandler("shadowban", self.shadowban_user))
        self.application.add_handler(CommandHandler("stats", self.generate_stats))
        self.application.add_handler(CommandHandler("scan", self.scan_command))
        
        # Message handlers
        self.application.add_handler(MessageHandler(filters.TEXT & ~filters.COMMAND, self.check_message))
        self.application.add_handler(MessageHandler(filters.VOICE, self.process_voice_message))
        self.application.add_handler(MessageHandler(filters.PHOTO, self.process_photo_message))
        
        # Callback query handler for CrowdCheck button
        self.application.add_handler(CallbackQueryHandler(self.handle_crowd_check, pattern='^crowdcheck_'))
        
        # Poll handler for processing poll updates
        self.application.add_handler(PollHandler(self.handle_poll_update))
        
        # Error handler
        self.application.add_error_handler(self.error_handler)

    # ------------------------------------------------
    #                ASYNC HANDLERS
    # ------------------------------------------------
    
    async def start(self, update: Update, context: CallbackContext):
        """Send a welcome message when the command /start is issued"""
        user = update.effective_user
        await update.message.reply_text(
            f"Hi {user.first_name}! I am WhateverBot, here to help combat misinformation.\n"
            f"Use /help to see available commands.",
            parse_mode=ParseMode.HTML
        )
    
    async def help(self, update: Update, context: CallbackContext):
        """Send help information when the command /help is issued."""
        help_text = (
            "<b>MisinfoBot Commands:</b>\n\n"
            "<b>/start</b> – Start the bot\n"
            "<b>/help</b> – Show this help message\n"
            "<b>/report</b> – Generate a report of recent misinformation\n"
            "<b>/mute &lt;username&gt;</b> – Mute a user\n"
            "<b>/unmute &lt;username&gt;</b> – Unmute a user\n"
            "<b>/kick &lt;username&gt;</b> – Kick a user from the group\n"
            "<b>/ban &lt;username&gt;</b> – Ban a user from the group\n"
            "<b>/shadowban &lt;username&gt;</b> – Shadowban a user\n\n"
            "<b>/scan &lt;text&gt;</b> – Force an external API scan on the provided text. "
            "If no known match is found in the database, an API call will be made and the result stored.\n\n"
            "Normal messages are scanned against our known misinformation database only.\n\n"
            "<b>CrowdCheck Feature:</b> Use the CrowdCheck button to initiate a group vote on any analysis message."
        )
        await update.message.reply_text(help_text, parse_mode=ParseMode.HTML)

    async def check_message(self, update: Update, context: CallbackContext):
        """
        Handle incoming normal text messages.
        For normal messages, the bot will only check against the known misinformation database
        (using fuzzy matching) and will not call external APIs.
        """
        message = update.message.text
        chat_id = update.effective_chat.id
        user_id = update.effective_user.id
        message_id = update.message.message_id

        # Update message statistics
        if chat_id not in self.message_stats:
            self.message_stats[chat_id] = {"total": 0, "misinfo": 0}
        self.message_stats[chat_id]["total"] += 1
        
        logger.info(f"📩 Received message from {user_id} in chat {chat_id}: {message}")
        
        if self.user_manager.is_shadowbanned(user_id, chat_id):
            logger.info(f"Ignoring message from shadowbanned user {user_id}")
            return
        
        # For normal messages, do not force external API call
        result = self.fact_checker.check_message(message, force_api=False)
        
        # If a known misinformation match is found, send a warning
        if result['is_misinformation']:
            misinfo_id = self.db.log_misinformation(user_id, chat_id, message, result['confidence'], result['category'])
            offense_count = self.user_manager.increment_offense(user_id, chat_id)
            self.message_stats[chat_id]["misinfo"] += 1
            
            try:
                gemini_analysis = ""
                if result.get('gemini_analysis'):
                    gemini_analysis = await self._format_gemini_analysis(result['gemini_analysis'])
                
                warning_message = (
                    "<b>⚠️ Potential Misinformation Detected ⚠️</b>\n\n"
                    f"<b>Category:</b> {result['category']}\n"
                    f"<b>Confidence:</b> {result['confidence']:.2f}%\n"
                    f"<b>Offense Count:</b> {offense_count}\n\n"
                    f"<b>Fact Check:</b> {result['fact_check']}\n"
                )
                if gemini_analysis:
                    warning_message += f"<b>🤖 AI Analysis:</b> {gemini_analysis}\n\n"
                warning_message += "ℹ️ Please verify information before sharing."
                
                # Add CrowdCheck button (using 0 as placeholder for non-API scan)
                keyboard = [
                    [InlineKeyboardButton("👥 CrowdCheck", callback_data=f"crowdcheck_{message_id}_0")]
                ]
                reply_markup = InlineKeyboardMarkup(keyboard)
                
                await context.bot.send_message(
                    chat_id=chat_id,
                    text=warning_message,
                    parse_mode=ParseMode.HTML,
                    reply_to_message_id=message_id,
                    reply_markup=reply_markup
                )
                

                # Automatically take action based on offense count
                if offense_count == 3:
                    await self._mute_user(chat_id, user_id, duration=60)  # Mute for 1 hour
                    action_taken = "Muted for 1 hour"
                elif offense_count == 5:
                    await self._kick_user(chat_id, user_id)  # Kick user
                    action_taken = "Kicked from the group"
                elif offense_count >= 7:
                    await self._ban_user(chat_id, user_id)  # Ban user
                    action_taken = "Permanently banned"
                else:
                    action_taken = "Warning issued"

                logger.info(f"User {user_id} reached {offense_count} offenses. Action taken: {action_taken}")

                
                logger.info(f"Sent misinformation warning to user {user_id} in chat {chat_id}. Offense count: {offense_count}")
            except TelegramError as e:
                logger.error(f"Failed to send warning message: {e}")
        else:
            # Optionally, if you want to send analysis even for non-misinfo messages, you could add that here.
            # For now, we do nothing.
            pass

    async def scan_command(self, update: Update, context: CallbackContext):
        """
        /scan command: Force an external API scan on the provided text.
        This bypasses the database fuzzy matching. If misinformation is detected,
        the result is standardized and stored in the DB using the original text as key.
        """
        if not context.args:
            await update.message.reply_text("Usage: /scan <text to analyze>", parse_mode=ParseMode.HTML)
            return

        text_to_scan = " ".join(context.args)
        logger.info(f"Explicitly scanning text (via /scan): {text_to_scan}")
        
        # Force external API scan
        result = self.fact_checker.check_message(text_to_scan, force_api=True)
        
        if result['is_misinformation']:
            self.db.log_misinformation(update.effective_user.id, update.effective_chat.id, text_to_scan, result['confidence'], result['category'])
        
        response = (
            f"<b>Misinformation Analysis (Forced API Scan)</b>\n\n"
            f"<b>Is Misinformation:</b> {result['is_misinformation']}\n"
            f"<b>Confidence:</b> {result['confidence']:.2f}%\n"
            f"<b>Category:</b> {result['category'] if result['category'] else 'N/A'}\n"
            f"<b>Fact Check:</b> {result['fact_check'] if result['fact_check'] else 'N/A'}\n"
        )
        if result.get("gemini_analysis"):
            response += f"<b>AI Analysis:</b> {result['gemini_analysis']}\n"
        await update.message.reply_text(response, parse_mode=ParseMode.HTML)

    async def handle_crowd_check(self, update: Update, context: CallbackContext):
        """Handle CrowdCheck button clicks."""
        query = update.callback_query
        await query.answer()
        data_parts = query.data.split('_')
        if len(data_parts) < 3:
            logger.error(f"Invalid callback data: {query.data}")
            return
        original_message_id = int(data_parts[1])
        misinfo_id = int(data_parts[2])
        chat_id = update.effective_chat.id
        bot_message_id = query.message.message_id
        user_id = update.effective_user.id

        # Create a crowd check record (assumes you have implemented this in your DB module)
        check_id = self.db.create_crowd_check(
            chat_id=chat_id,
            original_message_id=original_message_id,
            bot_message_id=bot_message_id,
            misinformation_id=misinfo_id if misinfo_id > 0 else None,
            expires_in_minutes=60
        )
        
        # Create and send the poll (using placeholder text for original message)
        try:
            # Get the original message to include in the poll question
            try:
                # Since we can't directly fetch the original message content in python-telegram-bot,
                # we'll use a placeholder text for the poll question
                # original_text = "[Original message]" 
                
                # Truncate if too long
                if original_text and len(original_text) > 100:
                    original_text = original_text[:97] + "..."
            except Exception as e:
                logger.error(f"Error retrieving original message: {e}")
                #original_text = "[Original message not available]"
            
            question = f"Is this message misinformation?\n\n\"{original_text}\""
            options = ["True (Not Misinformation)", "Misleading", "False (Is Misinformation)"]
            poll_message = await context.bot.send_poll(
                chat_id=chat_id,
                question=question,
                options=options,
                is_anonymous=False,
                allows_multiple_answers=False,
                open_period=20,
                reply_to_message_id=bot_message_id
            )
            self.db.update_crowd_check_poll(
                check_id=check_id,
                poll_message_id=poll_message.message_id,
                poll_id=poll_message.poll.id
            )
            await context.bot.send_message(
                chat_id=chat_id,
                text="<b>👥 CrowdCheck Poll Created</b>\n\nGroup members can now vote on whether this message contains misinformation. The poll will be open for 20 seconds.",
                parse_mode=ParseMode.HTML,
                reply_to_message_id=poll_message.message_id
            )
            context.job_queue.run_once(
                self.process_poll_results,
                20,
                data={'poll_id': poll_message.poll.id, 'chat_id': chat_id, 'poll_message_id': poll_message.message_id}
            )
            logger.info(f"Created CrowdCheck poll for message {original_message_id} in chat {chat_id}")
        except TelegramError as e:
            logger.error(f"Failed to create poll: {e}")

    async def handle_poll_update(self, update: Update, context: CallbackContext):
        """Handle poll updates (when users vote)."""
        poll = update.poll
        poll_id = poll.id
        crowd_check = self.db.get_crowd_check(poll_id=poll_id)
        if not crowd_check:
            logger.error(f"Received poll update for unknown poll: {poll_id}")
            return
        true_votes = poll.options[0].voter_count
        misleading_votes = poll.options[1].voter_count
        false_votes = poll.options[2].voter_count
        total_votes = true_votes + misleading_votes + false_votes
        self.db.update_crowd_check_votes(
            poll_id=poll_id,
            true_votes=true_votes,
            misleading_votes=misleading_votes,
            false_votes=false_votes,
            total_votes=total_votes
        )
        logger.info(f"Updated vote counts for poll {poll_id}: True={true_votes}, Misleading={misleading_votes}, False={false_votes}")
    
    async def process_poll_results(self, context: CallbackContext):
        """Process the results of a completed poll"""
        job_data = context.job.data
        poll_id = job_data.get('poll_id')
        chat_id = job_data.get('chat_id')
        poll_message_id = job_data.get('poll_message_id')
        
        # Get the crowd check record
        crowd_check = self.db.get_crowd_check(poll_id=poll_id)
        if not crowd_check:
            logger.error(f"Failed to find crowd check record for poll {poll_id}")
            return
        
        # Get vote counts
        true_votes = crowd_check['true_votes']
        misleading_votes = crowd_check['misleading_votes']
        false_votes = crowd_check['false_votes']
        total_votes = crowd_check['total_votes']
        
        # Skip if no votes
        if total_votes == 0:
            await context.bot.send_message(
                chat_id=chat_id,
                text="<b>👥 CrowdCheck Results</b>\n\nNo votes were cast during the voting period.",
                parse_mode=ParseMode.HTML,
                reply_to_message_id=poll_message_id
            )
            return
        
        # Calculate percentages
        true_percent = (true_votes / total_votes) * 100
        misleading_percent = (misleading_votes / total_votes) * 100
        false_percent = (false_votes / total_votes) * 100
        
        # Determine the final decision based on voting thresholds
        final_decision = None
        action_taken = None
        
        if false_percent >= 80:
            # 80%+ voted "False" (is misinformation)
            final_decision = "False"
            action_taken = "Message deleted and warning sent"
            
            # Try to delete the original message
            try:
                await context.bot.delete_message(
                    chat_id=chat_id,
                    message_id=crowd_check['original_message_id']
                )
                
                # Send warning to the user
                await context.bot.send_message(
                    chat_id=chat_id,
                    text="<b>⚠️ Message Removed by Community Vote ⚠️</b>\n\nYour message was determined to contain misinformation by group members and has been removed.",
                    parse_mode=ParseMode.HTML
                )
                
            except TelegramError as e:
                logger.error(f"Failed to delete message: {e}")
                action_taken = "Failed to delete message"
                
        elif misleading_percent >= 50:
            # 50-80% voted "Misleading"
            final_decision = "Misleading"
            action_taken = "Warning sent"
            
            # Send warning about misleading content
            await context.bot.send_message(
                chat_id=chat_id,
                text="<b>⚠️ Potentially Misleading Content ⚠️</b>\n\nGroup members have voted that this message may contain misleading information. Please verify facts before sharing.",
                parse_mode=ParseMode.HTML,
                reply_to_message_id=crowd_check['original_message_id']
            )
            
        elif true_votes > misleading_votes and true_votes > false_votes:
            # Majority voted "True" (not misinformation)
            final_decision = "True"
            action_taken = "Message reclassified as not misinformation"
            
            # Send message about reclassification
            await context.bot.send_message(
                chat_id=chat_id,
                text="<b>✅ Message Verified by Community</b>\n\nGroup members have voted that this message does not contain misinformation.",
                parse_mode=ParseMode.HTML,
                reply_to_message_id=crowd_check['original_message_id']
            )
        
        # Update the crowd check record with the final decision
        self.db.complete_crowd_check(
            poll_id=poll_id,
            final_decision=final_decision,
            action_taken=action_taken
        )
        
        # Send a summary of the results
        results_message = (
            f"<b>👥 CrowdCheck Results</b>\n\n"
            f"Total Votes: {total_votes}\n"
            f"• True (Not Misinformation): {true_votes} ({true_percent:.1f}%)\n"
            f"• Misleading: {misleading_votes} ({misleading_percent:.1f}%)\n"
            f"• False (Is Misinformation): {false_votes} ({false_percent:.1f}%)\n\n"
            f"<b>Decision:</b> {final_decision}\n"
            f"<b>Action:</b> {action_taken}"
        )
        
        await context.bot.send_message(
            chat_id=chat_id,
            text=results_message,
            parse_mode=ParseMode.HTML,
            reply_to_message_id=poll_message_id
        )
        
        logger.info(f"Processed poll results for {poll_id}: Decision={final_decision}, Action={action_taken}")

    async def process_voice_message(self, update: Update, context: CallbackContext):
        """Process voice messages by transcribing and fact-checking"""
        await update.message.reply_text("Voice message processing is not yet implemented.", parse_mode=ParseMode.HTML)
    
    async def process_photo_message(self, update: Update, context: CallbackContext):
        """
        Process photo messages by:
         - Extracting text from the image using Google Vision.
         - Forcing an external API scan on the extracted text.
         This ensures image scanning always uses the external APIs.
        """
        user_id = update.effective_user.id
        chat_id = update.effective_chat.id
        message_id = update.message.message_id
        
        if self.user_manager.is_shadowbanned(user_id, chat_id):
            logger.info(f"Ignoring photo from shadowbanned user {user_id}")
            return
        
        photo = update.message.photo[-1]
        photo_file = await context.bot.get_file(photo.file_id)
        photo_url = photo_file.file_path
        
        # Always force an external API scan for images
        deepfake_result = self.fact_checker.check_deepfake(photo_url)
        text_result = self.fact_checker.analyze_image_text(image_url=photo_url)
        is_problematic = deepfake_result['is_deepfake'] or text_result['is_misinformation']
        
        if is_problematic:
            if deepfake_result['is_deepfake'] and text_result['is_misinformation']:
                if deepfake_result['confidence'] >= text_result['confidence']:
                    category = "Deepfake"
                    confidence = deepfake_result['confidence']
                    analysis = deepfake_result['analysis']
                    warning_title = "Potential Manipulated Media Detected"
                    warning_desc = "This photo may be manipulated or a deepfake."
                else:
                    category = text_result['category'] or "Text Misinformation"
                    confidence = text_result['confidence']
                    analysis = text_result['fact_check']
                    warning_title = "Potential Misinformation in Image Text"
                    warning_desc = "The text in this image may contain misinformation."
            elif deepfake_result['is_deepfake']:
                category = "Deepfake"
                confidence = deepfake_result['confidence']
                analysis = deepfake_result['analysis']
                warning_title = "Potential Manipulated Media Detected"
                warning_desc = "This photo may be manipulated or a deepfake."
            else:
                category = text_result['category'] or "Text Misinformation"
                confidence = text_result['confidence']
                analysis = text_result['fact_check']
                warning_title = "Potential Misinformation in Image Text"
                warning_desc = "The text in this image may contain misinformation."
            
            self.db.log_misinformation(user_id, chat_id, "[PHOTO]", confidence, category)
            offense_count = self.user_manager.increment_offense(user_id, chat_id)
            
            try:
                gemini_analysis = ""
                if text_result.get('gemini_analysis'):
                    gemini_analysis = await self._format_gemini_analysis(text_result['gemini_analysis'])
                
                warning_message = (
                    f"<b>⚠️ {warning_title} ⚠️</b>\n\n"
                    f"{warning_desc}\n\n"
                    "<b>📊 Detection Details</b>\n"
                    f"• Category: {category}\n"
                    f"• Confidence: {confidence:.2f}%\n"
                    f"• Offense Count: {offense_count}\n\n"
                )
                if gemini_analysis:
                    warning_message += f"<b>🤖 AI Analysis:</b> {gemini_analysis}\n\n"
                warning_message += "ℹ️ Please verify media before sharing. This helps maintain a trustworthy environment."
                
                # Add CrowdCheck button (using offense count as a placeholder if needed)
                keyboard = [
                    [InlineKeyboardButton("👥 CrowdCheck", callback_data=f"crowdcheck_{message_id}_{offense_count}")]
                ]
                reply_markup = InlineKeyboardMarkup(keyboard)
                
                await context.bot.send_message(
                    chat_id=chat_id,
                    text=warning_message,
                    parse_mode=ParseMode.HTML,
                    reply_to_message_id=message_id,
                    reply_markup=reply_markup
                )
                logger.info(f"Sent misinformation warning to user {user_id} in chat {chat_id}. Offense count: {offense_count}")
            except TelegramError as e:
                logger.error(f"Failed to send warning message: {e}")
        else:
            logger.info(f"Photo from user {user_id} passed all misinformation checks")
            if text_result.get('extracted_text'):
                extracted = text_result['extracted_text'][:100]
                logger.info(f"Extracted text from image: {extracted}...")
                if text_result.get('gemini_analysis'):
                    try:
                        gemini_analysis = await self._format_gemini_analysis(text_result['gemini_analysis'])
                        info_message = (
                            f"<b>📝 Image Analysis Results</b> 📝\n\n"
                            f"<b>🤖 AI Analysis:</b> {gemini_analysis}\n\n"
                        )
                        keyboard = [
                            [InlineKeyboardButton("👥 CrowdCheck", callback_data=f"crowdcheck_{message_id}_0")]
                        ]
                        reply_markup = InlineKeyboardMarkup(keyboard)
                        await context.bot.send_message(
                            chat_id=chat_id,
                            text=info_message,
                            parse_mode=ParseMode.HTML,
                            reply_to_message_id=message_id,
                            reply_markup=reply_markup
                        )
                    except TelegramError as e:
                        logger.error(f"Failed to send analysis message: {e}")
    
    async def _format_gemini_analysis(self, analysis_text):
        """Format Gemini analysis text for better readability."""
        if not isinstance(analysis_text, str):
            try:
                analysis_text = str(analysis_text)
            except Exception as e:
                logger.error(f"Error converting analysis to string: {e}")
                return "Analysis unavailable"
        # Remove any HTML tags
        analysis_text = re.sub(r'<[^>]+>', '', analysis_text)
        formatted_text = ""
        verification_match = re.search(r"Verification Status:\s*([^\n]+)", analysis_text)
        if verification_match:
            status = verification_match.group(1).strip().replace("<", "&lt;").replace(">", "&gt;")
            formatted_text += f"• Status: {status}\n"
        source_match = re.search(r"Source Check:\s*([^\n]+)", analysis_text)
        if source_match:
            sources = source_match.group(1).strip().replace("<", "&lt;").replace(">", "&gt;")
            formatted_text += f"• Sources: {sources}\n"
        analysis_match = re.search(r"Analysis:\s*([^\n]+(?:\n[^\n]+)*)", analysis_text)
        if analysis_match:
            analysis = analysis_match.group(1).strip().replace("<", "&lt;").replace(">", "&gt;")
            sentences = re.split(r'(?<=[.!?])\s+', analysis)
            if len(sentences) > 3:
                analysis = ' '.join(sentences[:3]) + '...'
            formatted_text += f"• Summary: {analysis}"
        if not formatted_text:
            safe_text = analysis_text.replace("<", "&lt;").replace(">", "&gt;")
            sentences = re.split(r'(?<=[.!?])\s+', safe_text)
            formatted_text = ' '.join(sentences[:3]) + '...' if len(sentences) > 3 else safe_text
        return formatted_text
    
    async def generate_report(self, update: Update, context: CallbackContext):
        """Generate and send a report of recent misinformation"""
        chat_id = update.effective_chat.id
        user_id = update.effective_user.id
        chat_member = await context.bot.get_chat_member(chat_id, user_id)
        
        if chat_member.status not in ['creator', 'administrator']:
            await update.message.reply_text("Sorry, only administrators can generate reports.", parse_mode=ParseMode.HTML)
            return
        
        # Generate the report
        report = self.report_generator.generate_daily_report(chat_id)
        
        # Send the report
        await update.message.reply_text(
            f"<b>Daily Misinformation Report</b>\n\n{report}",
            parse_mode=ParseMode.HTML
        )
    
    async def shadowban_user(self, update: Update, context: CallbackContext):
        """Shadowban a user"""
        await self._admin_action(update, context, self.user_manager.shadowban_user, "shadowbanned")
    
    async def _admin_action(self, update: Update, context: CallbackContext, action_func, action_name):
        """Helper method to handle admin actions"""
        chat_id = update.effective_chat.id
        user_id = update.effective_user.id
        chat_member = await context.bot.get_chat_member(chat_id, user_id)
        
        if chat_member.status not in ['creator', 'administrator']:
            await update.message.reply_text("Sorry, only administrators can perform this action.", parse_mode=ParseMode.HTML)
            return
        
        if not context.args:
            await update.message.reply_text(f"Please provide a username. Example: /{action_name.lower()} @username", parse_mode=ParseMode.HTML)
            return
        
        target_username = context.args[0]
        
        try:
            target_user = await context.bot.get_chat(target_username)
            target_user_id = target_user.id
            
            target_member = await context.bot.get_chat_member(chat_id, target_user_id)
            if target_member.status == 'creator':
                await update.message.reply_text(f"Cannot {action_name.lower()} the chat owner.", parse_mode=ParseMode.HTML)
                return
            
            success = action_func(target_user_id, chat_id)
            
            if success:
                await update.message.reply_text(f"User {target_username} has been {action_name}.", parse_mode=ParseMode.HTML)
            else:
                await update.message.reply_text(f"Failed to {action_name.lower()} user {target_username}.", parse_mode=ParseMode.HTML)
                
        except TelegramError as e:
            await update.message.reply_text(f"Error: {str(e)}", parse_mode=ParseMode.HTML)

    async def generate_stats(self, update: Update, context: CallbackContext):
        """Generate and send chat statistics"""
        chat_id = update.effective_chat.id

        if chat_id not in self.message_stats:
            await update.message.reply_text("No message data available for this group yet.", parse_mode=ParseMode.HTML)
            return

        total_messages = self.message_stats[chat_id]["total"]
        misinfo_count = self.message_stats[chat_id]["misinfo"]

        if total_messages == 0:
            await update.message.reply_text("No messages recorded in this chat.", parse_mode=ParseMode.HTML)
            return

        misinfo_percentage = (misinfo_count / total_messages) * 100

        stats_message = (
            f"<b>📊 Group Statistics</b>\n\n"
            f"Total Messages: {total_messages}\n"
            f"Misinformation Messages: {misinfo_count}\n"
            f"Percentage of Misinformation: {misinfo_percentage:.2f}%\n\n"
            "Help maintain a fact-checked group! 🛑"
        )
        await update.message.reply_text(stats_message, parse_mode=ParseMode.HTML)

    async def mute_user(self, update: Update, context: CallbackContext):
        """Mute a user in the group (prevents them from sending messages)."""
        chat_id = update.effective_chat.id
        user_id = update.effective_user.id
        chat_member = await context.bot.get_chat_member(chat_id, user_id)

        if chat_member.status not in ['creator', 'administrator']:
            await update.message.reply_text("Sorry, only admins can mute users.", parse_mode=ParseMode.HTML)
            return

        if not context.args:
            await update.message.reply_text("Usage: /mute @username [duration in minutes]", parse_mode=ParseMode.HTML)
            return

        target_username = context.args[0]
        duration = int(context.args[1]) if len(context.args) > 1 else 10  # Default mute for 10 minutes

        try:
            target_user = await context.bot.get_chat_member(chat_id, target_username)
            target_user_id = target_user.user.id

            # **PREVENT BOT FROM MUTING ITSELF**
            if target_user_id == context.bot.id:
                await update.message.reply_text("I cannot mute myself!", parse_mode=ParseMode.HTML)
                return

            until_date = int((datetime.utcnow() + timedelta(minutes=duration)).timestamp())  # Convert to UNIX timestamp

            await context.bot.restrict_chat_member(
                chat_id,
                target_user_id,
                permissions=ChatPermissions(can_send_messages=False),
                until_date=until_date
            )

            await update.message.reply_text(f"User {target_username} has been muted for {duration} minutes.", parse_mode=ParseMode.HTML)

        except TelegramError as e:
            await update.message.reply_text(f"Error: {str(e)}", parse_mode=ParseMode.HTML)



    async def unmute_user(self, update: Update, context: CallbackContext):
        """Unmute a previously muted user."""
        chat_id = update.effective_chat.id
        user_id = update.effective_user.id
        chat_member = await context.bot.get_chat_member(chat_id, user_id)

        if chat_member.status not in ['creator', 'administrator']:
            await update.message.reply_text("Sorry, only admins can unmute users.", parse_mode=ParseMode.HTML)
            return

        if not context.args:
            await update.message.reply_text("Usage: /unmute @username", parse_mode=ParseMode.HTML)
            return

        target_username = context.args[0]

        try:
            target_user = await context.bot.get_chat(target_username)
            target_user_id = target_user.id

            await context.bot.restrict_chat_member(
                chat_id,
                target_user_id,
                permissions=ChatPermissions(
                    can_send_messages=True,
                    can_send_media_messages=True,
                    can_send_other_messages=True,
                    can_add_web_page_previews=True
                )
            )

            await update.message.reply_text(f"User {target_username} has been unmuted.", parse_mode=ParseMode.HTML)

        except TelegramError as e:
            await update.message.reply_text(f"Error: {str(e)}", parse_mode=ParseMode.HTML)


    async def kick_user(self, update: Update, context: CallbackContext):
        """Kick a user from the group (they can rejoin)."""
        chat_id = update.effective_chat.id
        user_id = update.effective_user.id
        chat_member = await context.bot.get_chat_member(chat_id, user_id)

        if chat_member.status not in ['creator', 'administrator']:
            await update.message.reply_text("Sorry, only admins can kick users.", parse_mode=ParseMode.HTML)
            return

        if not context.args:
            await update.message.reply_text("Usage: /kick @username", parse_mode=ParseMode.HTML)
            return

        target_username = context.args[0]

        try:
            target_member = await context.bot.get_chat_member(chat_id, target_username)
            target_user_id = target_member.user.id

            # **PREVENT BOT FROM MUTING ITSELF**
            if target_user_id == context.bot.id:
                await update.message.reply_text("I cannot mute myself!", parse_mode=ParseMode.HTML)
                return

            if target_member.status == 'creator':
                await update.message.reply_text("Cannot kick the chat owner.", parse_mode=ParseMode.HTML)
                return

            await context.bot.ban_chat_member(chat_id, target_user_id)
            await context.bot.unban_chat_member(chat_id, target_user_id)  # Allows rejoining

            await update.message.reply_text(f"User {target_username} has been kicked from the chat.", parse_mode=ParseMode.HTML)

        except TelegramError as e:
            await update.message.reply_text(f"Error: {str(e)}", parse_mode=ParseMode.HTML)



    async def ban_user(self, update: Update, context: CallbackContext):
        """Ban a user from the group permanently."""
        chat_id = update.effective_chat.id
        user_id = update.effective_user.id
        chat_member = await context.bot.get_chat_member(chat_id, user_id)

        if chat_member.status not in ['creator', 'administrator']:
            await update.message.reply_text("Sorry, only admins can ban users.", parse_mode=ParseMode.HTML)
            return

        if not context.args:
            await update.message.reply_text("Usage: /ban @username", parse_mode=ParseMode.HTML)
            return

        target_username = context.args[0]

        try:
            target_user = await context.bot.get_chat(target_username)
            target_user_id = target_user.id

            # **PREVENT BOT FROM MUTING ITSELF**
            if target_user_id == context.bot.id:
                await update.message.reply_text("I cannot mute myself!", parse_mode=ParseMode.HTML)
                return

            await context.bot.ban_chat_member(chat_id, target_user_id)

            await update.message.reply_text(f"User {target_username} has been permanently banned.", parse_mode=ParseMode.HTML)

        except TelegramError as e:
            await update.message.reply_text(f"Error: {str(e)}", parse_mode=ParseMode.HTML)

    async def _mute_user(self, chat_id: int, user_id: int, duration: int):
        """Mute a user for a specific duration (in minutes)."""
        try:
            until_date = int((datetime.utcnow() + timedelta(minutes=duration)).timestamp())  # UNIX timestamp

            await self.application.bot.restrict_chat_member(
                chat_id,
                user_id,
                permissions=ChatPermissions(can_send_messages=False),
                until_date=until_date
            )

            await self.application.bot.send_message(
                chat_id,
                text=f"🚨 User <a href='tg://user?id={user_id}'>[User]</a> has been muted for {duration} minutes due to repeated misinformation.",
                parse_mode=ParseMode.HTML
            )

            logger.info(f"Muted user {user_id} in chat {chat_id} for {duration} minutes.")
        except TelegramError as e:
            logger.error(f"Failed to mute user {user_id}: {e}")

    async def _kick_user(self, chat_id: int, user_id: int):
        """Kick a user from the group."""
        try:
            await self.application.bot.ban_chat_member(chat_id, user_id)
            await self.application.bot.unban_chat_member(chat_id, user_id)  # Allows rejoining

            await self.application.bot.send_message(
                chat_id,
                text=f"🚨 User <a href='tg://user?id={user_id}'>[User]</a> has been kicked from the group for repeated misinformation.",
                parse_mode=ParseMode.HTML
            )

            logger.info(f"Kicked user {user_id} from chat {chat_id}.")
        except TelegramError as e:
            logger.error(f"Failed to kick user {user_id}: {e}")

    async def _ban_user(self, chat_id: int, user_id: int):
        """Permanently ban a user from the group."""
        try:
            await self.application.bot.ban_chat_member(chat_id, user_id)

            await self.application.bot.send_message(
                chat_id,
                text=f"🚨 User <a href='tg://user?id={user_id}'>[User]</a> has been permanently banned for repeated misinformation.",
                parse_mode=ParseMode.HTML
            )

            logger.info(f"Banned user {user_id} from chat {chat_id}.")
        except TelegramError as e:
            logger.error(f"Failed to ban user {user_id}: {e}")

    
    async def error_handler(self, update: Update, context: CallbackContext):
        """Log errors caused by updates"""
        logger.error(f"Update {update} caused error {context.error}")
    
    def run(self):
        """Start the bot (sync entry point for the async application)"""
        self.application.run_polling()
        logger.info("Bot started polling")


if __name__ == '__main__':
    bot = MisinfoBot()
    bot.run()