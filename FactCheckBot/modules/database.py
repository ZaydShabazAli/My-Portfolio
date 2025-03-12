import os
import logging
from datetime import datetime
from sqlalchemy import create_engine, Column, Integer, String, Float, DateTime, Boolean, ForeignKey, Text
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, relationship
import difflib  # for fuzzy matching

logger = logging.getLogger(__name__)

Base = declarative_base()

# ------------------------
#       MODELS
# ------------------------
class User(Base):
    __tablename__ = 'users'
    
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, unique=True)
    username = Column(String(255), nullable=True)
    first_name = Column(String(255), nullable=True)
    last_name = Column(String(255), nullable=True)
    is_bot = Column(Boolean, default=False)
    created_at = Column(DateTime, default=datetime.now)
    updated_at = Column(DateTime, default=datetime.now, onupdate=datetime.now)
    
    # Relationships
    offenses = relationship("Offense", back_populates="user")
    misinformation = relationship("Misinformation", back_populates="user")

class Chat(Base):
    __tablename__ = 'chats'
    
    id = Column(Integer, primary_key=True)
    chat_id = Column(Integer, unique=True)
    title = Column(String(255), nullable=True)
    chat_type = Column(String(50))
    created_at = Column(DateTime, default=datetime.now)
    updated_at = Column(DateTime, default=datetime.now, onupdate=datetime.now)
    
    # Relationships
    offenses = relationship("Offense", back_populates="chat")
    misinformation = relationship("Misinformation", back_populates="chat")

class Misinformation(Base):
    __tablename__ = 'misinformation'
    
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey('users.user_id'))
    chat_id = Column(Integer, ForeignKey('chats.chat_id'))
    message = Column(Text)
    confidence = Column(Float)
    category = Column(String(100))
    created_at = Column(DateTime, default=datetime.now)
    
    # Relationships
    user = relationship("User", back_populates="misinformation")
    chat = relationship("Chat", back_populates="misinformation")

class Offense(Base):
    __tablename__ = 'offenses'
    
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey('users.user_id'))
    chat_id = Column(Integer, ForeignKey('chats.chat_id'))
    count = Column(Integer, default=0)
    is_muted = Column(Boolean, default=False)
    is_banned = Column(Boolean, default=False)
    is_shadowbanned = Column(Boolean, default=False)
    last_offense_at = Column(DateTime, default=datetime.now)
    
    # Relationships
    user = relationship("User", back_populates="offenses")
    chat = relationship("Chat", back_populates="offenses")

class KnownMisinformation(Base):
    __tablename__ = 'known_misinformation'
    
    id = Column(Integer, primary_key=True)
    pattern = Column(Text)
    fact_check = Column(Text)
    category = Column(String(100))
    source = Column(String(255))
    created_at = Column(DateTime, default=datetime.now)
    updated_at = Column(DateTime, default=datetime.now, onupdate=datetime.now)

# ------------------------
#       DATABASE
# ------------------------
class Database:
    def __init__(self, db_url=None):
        """Initialize database connection."""
        if db_url is None:
            # Default to SQLite if no URL is provided
            db_url = os.getenv('DATABASE_URL', 'sqlite:///misinfobot.db')
        
        self.engine = create_engine(db_url)
        self.Session = sessionmaker(bind=self.engine)
        
        # Create tables if they don't exist
        Base.metadata.create_all(self.engine)
        
        # Seed database with some known misinformation if empty
        self._seed_known_misinformation()
    
    def _seed_known_misinformation(self):
        """Seed the database with some known misinformation patterns."""
        session = self.Session()
        
        # Check if we already have data
        if session.query(KnownMisinformation).count() == 0:
            examples = [
                {
                    'pattern': 'COVID-19 vaccines contain microchips',
                    'fact_check': 'COVID-19 vaccines do not contain microchips. This is a debunked conspiracy theory.',
                    'category': 'Health',
                    'source': 'WHO'
                },
                {
                    'pattern': '5G causes coronavirus',
                    'fact_check': 'There is no connection between 5G technology and COVID-19. This claim has been thoroughly debunked.',
                    'category': 'Technology',
                    'source': 'IEEE'
                },
                {
                    'pattern': 'The earth is flat',
                    'fact_check': 'The Earth is an oblate spheroid, not flat. This has been proven through countless scientific observations.',
                    'category': 'Science',
                    'source': 'NASA'
                }
            ]
            
            for example in examples:
                known_misinfo = KnownMisinformation(
                    pattern=example['pattern'],
                    fact_check=example['fact_check'],
                    category=example['category'],
                    source=example['source']
                )
                session.add(known_misinfo)
            
            session.commit()
            logger.info("Seeded database with known misinformation examples")
        
        session.close()
    
    # ------------------------------------------------
    #               GETTERS / CREATORS
    # ------------------------------------------------
    def get_user(self, user_id):
        """Get a user record by user_id."""
        session = self.Session()
        user = session.query(User).filter_by(user_id=user_id).first()
        session.close()
        return user
    
    def get_chat(self, chat_id):
        """Get a chat record by chat_id."""
        session = self.Session()
        chat = session.query(Chat).filter_by(chat_id=chat_id).first()
        session.close()
        return chat
    
    def get_offense(self, user_id, chat_id):
        """Get an offense record by user_id & chat_id, create if none."""
        session = self.Session()
        offense = session.query(Offense).filter_by(user_id=user_id, chat_id=chat_id).first()
        
        if not offense:
            # Ensure user & chat exist
            user = session.query(User).filter_by(user_id=user_id).first()
            if not user:
                user = User(user_id=user_id)
                session.add(user)
            
            chat = session.query(Chat).filter_by(chat_id=chat_id).first()
            if not chat:
                chat = Chat(chat_id=chat_id, chat_type='unknown')
                session.add(chat)
            
            offense = Offense(user_id=user_id, chat_id=chat_id, count=0)
            session.add(offense)
            session.commit()
        
        result = {
            'id': offense.id,
            'user_id': offense.user_id,
            'chat_id': offense.chat_id,
            'count': offense.count,
            'is_muted': offense.is_muted,
            'is_banned': offense.is_banned,
            'is_shadowbanned': offense.is_shadowbanned
        }
        session.close()
        return result
    
    # ------------------------------------------------
    #               OFFENSE / MISINFO
    # ------------------------------------------------
    def increment_offense(self, user_id, chat_id):
        """Increment the offense count for a user in a chat."""
        session = self.Session()
        offense = session.query(Offense).filter_by(user_id=user_id, chat_id=chat_id).first()
        
        if not offense:
            offense = Offense(user_id=user_id, chat_id=chat_id, count=1)
            session.add(offense)
        else:
            offense.count += 1
            offense.last_offense_at = datetime.now()
        
        session.commit()
        count = offense.count
        session.close()
        return count
    
    def log_misinformation(self, user_id, chat_id, message, confidence, category):
        """Log a detected misinformation message."""
        session = self.Session()
        
        # Ensure user & chat exist
        user = session.query(User).filter_by(user_id=user_id).first()
        if not user:
            user = User(user_id=user_id)
            session.add(user)
        
        chat = session.query(Chat).filter_by(chat_id=chat_id).first()
        if not chat:
            chat = Chat(chat_id=chat_id, chat_type='unknown')
            session.add(chat)
        
        misinfo = Misinformation(
            user_id=user_id,
            chat_id=chat_id,
            message=message,
            confidence=confidence,
            category=category
        )
        session.add(misinfo)
        session.commit()
        session.close()
    
    # ------------------------------------------------
    #          KNOWN MISINFORMATION
    # ------------------------------------------------
    def get_known_misinformation(self):
        """Return all known misinformation patterns as dictionaries."""
        session = self.Session()
        patterns = session.query(KnownMisinformation).all()
        result = []
        for p in patterns:
            result.append({
                'id': p.id,
                'pattern': p.pattern,
                'fact_check': p.fact_check,
                'category': p.category,
                'source': p.source
            })
        session.close()
        return result
    
    def find_similar_misinformation(self, text, threshold=0.7):
        """
        Fuzzy-match 'text' against known misinformation patterns.
        Returns a standardized dict if a match is found above 'threshold',
        otherwise None.
        """
        known = self.get_known_misinformation()
        best_match = None
        best_ratio = 0.0
        
        for record in known:
            ratio = difflib.SequenceMatcher(None, text.lower(), record['pattern'].lower()).ratio()
            if ratio > best_ratio:
                best_ratio = ratio
                best_match = record
        
        if best_match and best_ratio >= threshold:
            return self._standardize_record(best_match, best_ratio)
        return None
    
    def _standardize_record(self, record, similarity):
        """
        Convert a known misinformation record into the standard result dict.
        """
        return {
            'is_misinformation': True,
            'confidence': similarity * 100,  # Convert ratio to %
            'category': record['category'],
            'fact_check': record['fact_check'],
            'gemini_analysis': None
        }
    
    # OPTIONAL: store newly discovered external API facts
    def store_fact_check(self, text, result):
        """
        If you want to store newly discovered misinformation from external APIs
        into your known_misinformation table, define this method. Otherwise,
        you can remove or ignore it.
        """
        session = self.Session()
        try:
            if result['is_misinformation']:
                new_known = KnownMisinformation(
                    pattern=text,
                    fact_check=result.get('fact_check') or "No details",
                    category=result.get('category') or "Unverified",
                    source="API"  # or any other source
                )
                session.add(new_known)
                session.commit()
        except Exception as e:
            logger.error(f"Error storing new fact check in DB: {e}")
        finally:
            session.close()
    
    # ------------------------------------------------
    #         REPORTING & USER STATUS
    # ------------------------------------------------
    def get_daily_misinformation_stats(self, chat_id):
        """Get daily misinformation stats for a given chat."""
        session = self.Session()
        yesterday = datetime.now().replace(hour=0, minute=0, second=0, microsecond=0)
        
        misinfo = session.query(Misinformation).filter(
            Misinformation.chat_id == chat_id,
            Misinformation.created_at >= yesterday
        ).all()
        
        # Count by category
        categories = {}
        for m in misinfo:
            categories[m.category] = categories.get(m.category, 0) + 1
        
        # Top offenders
        top_offenders = session.query(
            User.user_id,
            User.username,
            User.first_name,
            User.last_name,
            Offense.count
        ).join(Offense).filter(
            Offense.chat_id == chat_id
        ).order_by(Offense.count.desc()).limit(5).all()
        
        result = {
            'total_count': len(misinfo),
            'categories': categories,
            'top_offenders': []
        }
        for o in top_offenders:
            result['top_offenders'].append({
                'user_id': o[0],
                'username': o[1],
                'first_name': o[2],
                'last_name': o[3],
                'offense_count': o[4]
            })
        
        session.close()
        return result
    
    def update_user_status(self, user_id, chat_id, status_type, value):
        """Update a user's status (muted, banned, shadowbanned)."""
        session = self.Session()
        offense = session.query(Offense).filter_by(user_id=user_id, chat_id=chat_id).first()
        
        if not offense:
            offense = Offense(user_id=user_id, chat_id=chat_id, count=0)
            session.add(offense)
        
        if status_type == 'muted':
            offense.is_muted = value
        elif status_type == 'banned':
            offense.is_banned = value
        elif status_type == 'shadowbanned':
            offense.is_shadowbanned = value
        
        session.commit()
        session.close()
        return True
    
    def get_user_status(self, user_id, chat_id):
        """Get a user's status in a chat."""
        session = self.Session()
        offense = session.query(Offense).filter_by(user_id=user_id, chat_id=chat_id).first()
        
        if not offense:
            result = {
                'is_muted': False,
                'is_banned': False,
                'is_shadowbanned': False,
                'offense_count': 0
            }
        else:
            result = {
                'is_muted': offense.is_muted,
                'is_banned': offense.is_banned,
                'is_shadowbanned': offense.is_shadowbanned,
                'offense_count': offense.count
            }
        session.close()
        return result
