import logging
from datetime import datetime

logger = logging.getLogger(__name__)

class UserManager:
    """
    Manages user actions (mute, ban, shadowban, etc.) by interfacing with the Database.
    
    The 'db' object is expected to have methods like:
        - get_user_status(user_id, chat_id) -> dict with keys:
            { 'is_muted': bool, 'is_banned': bool, 'is_shadowbanned': bool, 'offense_count': int }
        - increment_offense(user_id, chat_id) -> int (new offense count)
        - update_user_status(user_id, chat_id, status_type, value) -> bool
            (sets the specified status field to value)
    """

    def __init__(self, db):
        """Initialize the user manager with a database connection."""
        self.db = db
    
    def is_shadowbanned(self, user_id, chat_id):
        """
        Check if a user is shadowbanned in a specific chat.
        Returns True if shadowbanned, False otherwise.
        """
        user_status = self.db.get_user_status(user_id, chat_id)
        return user_status['is_shadowbanned']
    
    def increment_offense(self, user_id, chat_id):
        """
        Increment a user's offense count in the database
        and return the new offense count.
        """
        return self.db.increment_offense(user_id, chat_id)
    
    def mute_user(self, user_id, chat_id):
        """Mute a user in a chat."""
        return self.db.update_user_status(user_id, chat_id, 'muted', True)
    
    def unmute_user(self, user_id, chat_id):
        """Unmute a user in a chat."""
        return self.db.update_user_status(user_id, chat_id, 'muted', False)
    
    def ban_user(self, user_id, chat_id):
        """Ban a user from the chat."""
        return self.db.update_user_status(user_id, chat_id, 'banned', True)
    
    def unban_user(self, user_id, chat_id):
        """Unban a user from the chat."""
        return self.db.update_user_status(user_id, chat_id, 'banned', False)
    
    def shadowban_user(self, user_id, chat_id):
        """Shadowban a user in a chat."""
        return self.db.update_user_status(user_id, chat_id, 'shadowbanned', True)
    
    def unshadowban_user(self, user_id, chat_id):
        """Remove shadowban from a user in a chat."""
        return self.db.update_user_status(user_id, chat_id, 'shadowbanned', False)
    
    def kick_user(self, user_id, chat_id):
        """
        Kick a user from a chat.
        
        Note: The actual kicking must be handled by the bot using Telegram’s API.
        This method only updates the local database or logs for internal usage.
        """
        # If you want to reflect a "kicked" status in DB, do it here; otherwise, do nothing.
        logger.info(f"Simulating 'kick' action in DB for user {user_id} in chat {chat_id}.")
        return True
    
    def get_user_offenses(self, user_id, chat_id):
        """
        Get a user's offense count in a chat.
        """
        user_status = self.db.get_user_status(user_id, chat_id)
        return user_status['offense_count']
    
    def reset_user_offenses(self, user_id, chat_id):
        """
        Reset a user's offense count to zero.
        
        NOTE: This is a placeholder. Implement the actual database
        logic in the Database class and call it here.
        """
        logger.warning("reset_user_offenses() called, but not implemented.")
        pass
    
    def get_top_offenders(self, chat_id, limit=5):
        """
        Get the top offenders in a chat.
        
        NOTE: This is a placeholder. Implement in Database class if desired.
        """
        logger.warning("get_top_offenders() called, but not implemented.")
        pass
    
    def get_user_status_summary(self, user_id, chat_id):
        """
        Get a summary of the user's status (Muted, Banned, Shadowbanned) and offense count.
        
        Returns a dict:
        {
            'status': "Active" or comma-separated string of statuses,
            'offense_count': int
        }
        """
        user_status = self.db.get_user_status(user_id, chat_id)
        
        status_text = []
        if user_status['is_muted']:
            status_text.append("Muted")
        if user_status['is_banned']:
            status_text.append("Banned")
        if user_status['is_shadowbanned']:
            status_text.append("Shadowbanned")
        
        if not status_text:
            status_text = ["Active"]
        
        return {
            'status': ", ".join(status_text),
            'offense_count': user_status['offense_count']
        }
