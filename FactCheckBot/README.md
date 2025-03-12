Below is an **updated** README with **comprehensive setup** instructions, including **where to get each API key**, how to obtain **Google Cloud credentials** for image/OCR tasks, and how to place them into the `.env` file.

---

# Fake News Detective - Telegram Misinformation Detection Bot

This Telegram bot is designed to combat misinformation in group chats by **automatically** analyzing messages, detecting potential false information, and taking appropriate moderation actions.

## Features

- **Automatic Message Analysis**  
  Reads and analyzes all group messages using NLP.
- **Fact-checking**  
  Checks messages against known misinformation patterns; uses Google Fact Check Tools (optional) and generative AI (Gemini/OpenAI) for deeper analysis.
- **Misinformation Database**  
  Maintains a database of known misinformation for quick lookup.
- **User Management**  
  Tracks user offenses, can mute, ban, or shadowban users.
- **Deepfake Detection**  
  Uses **SightEngine** API to detect manipulated images and AI-generated faces.
- **Reporting**  
  Generates daily/weekly reports on misinformation spread.
- **Moderation Actions**  
  - Delete false messages automatically  
  - Mute flagged users  
  - Shadowban repeat offenders (hide messages without them knowing)  
  - Auto-kick users after multiple offenses  
  - Temporarily restrict messaging abilities

## Planned Features

- **Voice Message Transcription** and fact-checking  
- **Multi-language support**  
- **Public channel scanning** for misinformation  
- **Telegram web app integration**  
- **Crowdsourced voting system** for user-flagged content

---

## Setup Instructions

### 1. Prerequisites
- **Python 3.8+**
- A Telegram Bot Token (from [@BotFather](https://t.me/BotFather))

### 2. Clone & Install
1. **Clone** this repository:
   ```bash
   git clone https://github.com/yourusername/FakeNewsDetective.git
   cd FakeNewsDetective
   ```
2. **Install** dependencies:
   ```bash
   pip install -r requirements.txt
   ```
3. **Download** required NLTK & spaCy data:
   ```bash
   python -m nltk.downloader punkt stopwords
   python -m spacy download en_core_web_sm
   ```

### 3. Obtain & Configure API Keys
You can **enable** or **disable** various features based on which APIs you configure.

#### 3.1 Telegram Bot Token
1. Chat with [@BotFather](https://t.me/BotFather) → `"/newbot"`.
2. Copy the **token** (looks like `123456:ABC-...`).

#### 3.2 SightEngine (Deepfake Detection)
1. Sign up at **[SightEngine](https://sightengine.com/)**.
2. In your account dashboard, retrieve:
   - **`api_user`** 
   - **`api_secret`**

#### 3.3 OpenAI (Optional, for GPT-based analysis)
1. Go to **[OpenAI](https://platform.openai.com/)** and log in or sign up.
2. Under **API keys**, click **Create new secret key**.
3. Copy the key (starts with `sk-...`).

#### 3.4 Gemini (Google Generative AI) (Optional)
1. [Request Access or Sign Up for Google Generative AI](https://aistudio.google.com/) (limited access).
2. Retrieve your **Gemini API key** (starts with `AIza...`).

#### 3.5 Google Fact Check Tools API (Optional)
1. Go to **[Google Cloud Console](https://console.cloud.google.com/)** → create/select a project.
2. **Enable**: "**Fact Check Tools API**".
3. Under **APIs & Services → Credentials**, click "**Create Credentials**" → "**API Key**".
4. Copy the generated **API key**.

#### 3.6 Google Cloud Credentials (Optional, for Vision OCR)
1. Go to **[Google Cloud Console](https://console.cloud.google.com/)**.
2. Create a project or select an existing one.
3. **Enable the Vision API** under "**APIs & Services**".
4. **Create a Service Account**:
   - Give it a name/description.
   - Assign roles like **"Vision AI User"** or **"Editor"**.
5. Under "**Keys**" tab, click "**Add Key**" → "**Create new key**" → "**JSON**".
6. A `.json` file will **download** automatically (e.g. `my-project-XXXXX.json`).
7. Save this file to your project directory (e.g., `FakeNewsDetective/google-cloud-credentials.json`).

---

### 4. Create the `.env` File
Place a **`.env`** file in the **root** of your project with **all** your API keys (any you don’t have can be left out or disabled in your code). Example:

```ini
# TELEGRAM BOT
TELEGRAM_BOT_TOKEN="123456:ABCdefGhIjKlMnoPQR"

# (Optional) Google FactCheck Tools
GOOGLE_FACTCHECK_API_KEY="AIzaSyXXXX..."

# (Optional) OpenAI Key
OPENAI_API_KEY="sk-..."

# (Optional) Gemini (Google Generative AI)
GEMINI_API_KEY="AIzaSyXXXX..."

# (Optional) Google Cloud Vision credentials
GOOGLE_APPLICATION_CREDENTIALS="/absolute/path/to/google-cloud-credentials.json"

# (Optional) SightEngine credentials
SIGHTENGINE_API_USER="1111111111"
SIGHTENGINE_API_SECRET="xxxxxxyyyyyzzzzz"
```

**Ensure** you do not commit this file to GitHub – add `.env` to your `.gitignore`.

---

### 5. Run the Bot
Once your `.env` file is ready:
```bash
python bot.py
```
Check the console logs. You should see:
```
✅ Google Cloud authenticated with project: ...
Token is: ...
Bot started polling
```
Now **invite** your bot to a Telegram group or start a **private** chat with it.

---

## Bot Commands

- `/start` - Start the bot  
- `/help` - Show help message  
- `/report` - Generate a report of recent misinformation  
- `/mute <username>` - Mute a user  
- `/unmute <username>` - Unmute a user  
- `/kick <username>` - Kick a user from the group  
- `/ban <username>` - Ban a user from the group  
- `/shadowban <username>` - Shadowban a user  
- `/stats` - Get chat statistics

---

## Project Structure

```plaintext
FakeNewsDetective
│── bot.py
│── requirements.txt
│── .env
│── modules/
│    ├── database.py          # Database operations
│    ├── fact_checker.py      # Misinformation detection logic
│    ├── user_manager.py      # User management functions
│    ├── report_generator.py  # Report generation
│    └── ...
│── README.md
└── ...
```

1. **`bot.py`** – Main file that starts the bot.  
2. **`database.py`** – Handles all database interactions (user records, offense counts).  
3. **`fact_checker.py`** – Handles text/image analysis and API calls (FactCheck, Gemini, SightEngine).  
4. **`user_manager.py`** – Manage user statuses (muted, banned, shadowbanned).  
5. **`report_generator.py`** – Generate daily/weekly/other misinformation reports.

---

## License

MIT

---

## Disclaimer

This bot is designed as a tool to **help** combat misinformation, but it **is not infallible**.  
- **False positives and negatives** may occur.  
- Always use **human judgment** alongside automated tools.  
- By using any AI or third-party service for analysis, you must **comply** with their terms and policies.  

---

### **Enjoy the Bot!**  
If you need more help, open an **issue** or **pull request** on GitHub. Happy fact-checking!# Fake News Detective - Telegram Misinformation Detection Bot

This Telegram bot is designed to combat misinformation in group chats by automatically analyzing messages, detecting potential false information, and taking appropriate moderation actions.

## Features

- **Automatic Message Analysis**: Reads and analyzes all group messages
- **Fact-checking**: Checks messages against known misinformation patterns and uses NLP to detect potential false claims
- **Misinformation Database**: Maintains a database of known misinformation
- **User Management**: Tracks user offenses and can mute, ban, or shadowban users
- **Reporting**: Generates daily and weekly reports on misinformation spread
- **Moderation Actions**:
  - Delete false messages automatically
  - Mute flagged users
  - Shadowban repeat offenders (hide messages without notifying them)
  - Auto-kick users after multiple offenses
  - Temporarily restrict messaging abilities

## Features

- **Automatic Message Analysis**: Reads and analyzes all group messages
- **Fact-checking**: Checks messages against known misinformation patterns and uses NLP to detect potential false claims
- **Misinformation Database**: Maintains a database of known misinformation
- **User Management**: Tracks user offenses and can mute, ban, or shadowban users
- **Reporting**: Generates daily and weekly reports on misinformation spread
- **Deepfake Detection**: Uses SightEngine API to detect manipulated images and AI-generated faces
- **Moderation Actions**:
  - Delete false messages automatically
  - Mute flagged users
  - Shadowban repeat offenders (hide messages without notifying them)
  - Auto-kick users after multiple offenses
  - Temporarily restrict messaging abilities

## Planned Features

- Voice message transcription and fact-checking
- Multi-language support
- Public channel scanning for misinformation
- Telegram web app integration
- Crowdsourced voting system for user-flagged content

## Setup Instructions

### Prerequisites

- Python 3.8 or higher
- A Telegram Bot Token (obtained from [@BotFather](https://t.me/botfather))

### Installation

1. Clone this repository:
   ```
   git clone https://github.com/yourusername/FakeNewsDetective.git
   cd FakeNewsDetective
   ```

2. Install the required dependencies:
   ```
   pip install -r requirements.txt
   ```

3. Download required NLTK and spaCy data:
   ```
   python -m nltk.downloader punkt stopwords
   python -m spacy download en_core_web_sm
   ```

4. Create a `.env` file in the project root with your Telegram Bot Token and SightEngine API credentials:
   ```
   TELEGRAM_BOT_TOKEN=your_bot_token_here
   SIGHTENGINE_API_USER=your_sightengine_user_here
   SIGHTENGINE_API_SECRET=your_sightengine_secret_here
   ```

   You can obtain SightEngine API credentials by signing up at [SightEngine](https://sightengine.com/)

### Running the Bot

```
python bot.py
```

## Bot Commands

- `/start` - Start the bot
- `/help` - Show help message
- `/report` - Generate a report of recent misinformation
- `/mute <username>` - Mute a user
- `/unmute <username>` - Unmute a user
- `/kick <username>` - Kick a user from the group
- `/ban <username>` - Ban a user from the group
- `/shadowban <username>` - Shadowban a user

## Project Structure

- `bot.py` - Main bot file
- `modules/`
  - `database.py` - Database operations
  - `fact_checker.py` - Misinformation detection logic
  - `user_manager.py` - User management functions
  - `report_generator.py` - Report generation

## License

MIT

## Disclaimer

This bot is designed as a tool to help combat misinformation, but it should not be considered infallible. False positives and negatives may occur. Always use human judgment alongside automated tools.