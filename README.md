# 📱 Mark LV for Android

**A real voice assistant on your phone — built by GitHub, for free. You don't need to know any code.**

This folder contains everything needed to turn the Mark LV assistant into a real Android app (an `.apk` file) that you can install on your phone. GitHub builds it for you automatically, for free, on their computers.

What the app can do:
- 🎙️ You talk, it answers (in your language) — and it speaks the answer out loud
- ⌨️ You can also type instead of talking
- 🔦 Turn your flashlight on/off by voice
- 🔋 Tell you the battery level
- 📲 Open apps ("open WhatsApp") and websites ("open youtube.com")
- 🔍 Search Google for you
- 🧠 Remember things about you (your name, your city, your projects)
- 🪜 Same "model ladder" as the PC version — if one Gemini model is busy, the next one answers

What it cannot do: the PC version of Mark LV controls a computer (mouse, keyboard, files, WiFi). A phone is not a PC, so the app controls **the phone** instead. That is the only honest difference.

---

## 🧒 The guide — follow the numbers, don't read anything else

You need: a phone, and about 15 minutes. Everything is free.

### Step 1 — Make a GitHub account (a free "club" for code)

1. On your computer or phone, open **github.com**
2. Press the **Sign up** button (top right)
3. Type your email, make a password, follow the little puzzle
4. GitHub sends you a code to your email. Type that code in. Done ✅

### Step 2 — Get your free Gemini key (the app's "brain pass")

The app talks to Google's Gemini brain, and Google wants a "key" (a long password made of letters and numbers).

1. Open **aistudio.google.com/apikey** in your browser
2. Sign in with any Google account
3. Press **Create API key**
4. A long text appears. Press the **copy** button next to it
5. Paste it somewhere safe (like a note on your phone). You will paste it into the app later, in Step 7.

### Step 3 — Make a "repository" (a box on the internet for your files)

1. Go to **github.com** and log in
2. Press the **+** sign (top right) → **New repository**
3. Repository name: type **`MarkLV-Android`**
4. Choose **Public** (important!)
5. Press the green **Create repository** button

### Step 4 — Put this folder into the box (upload)

1. On the new page, find the link that says **"uploading an existing file"** and press it
2. On your computer: open the folder **`Mark-LV-Android`**, click once inside it, press **Ctrl+A** (this selects everything), then **drag all of it** into the GitHub upload box in your browser
3. ⚠️ Important: after the upload list fills, check that you can see a folder called **`.github`** in the list. If you don't see it, the build will not work — drag it in again by itself
4. Scroll down, press the big green **Commit changes** button
5. Go make tea ☕ — GitHub is now building your app on their computers

### Step 5 — Watch GitHub build the app

1. In your repository, press the **Actions** tab (top of the page)
2. You will see **"Build Android APK"** with a yellow dot 🟡 = it's working
3. Wait **5–10 minutes**. The yellow dot becomes a green checkmark ✅
4. If it turns red ❌ — see "Something broke?" below

### Step 6 — Download your app (the .apk file)

1. In your repository, look at the **right side** of the page → press **Releases**
2. Press the newest release at the top (like "Mark LV — build 1")
3. Under "Assets", press **MarkLV.apk** — it downloads
4. OR open the same page in your **phone's browser** and press MarkLV.apk there — the file lands straight on your phone (easier!)

### Step 7 — Install it on your phone

1. On your phone, open the **Files** app → **Downloads** → press **MarkLV.apk**
2. The phone says: "For your safety, you're not allowed to install unknown apps from this source." That is normal!
3. Press **Settings** on that popup → turn ON **"Allow from this source"** → go back
4. Press **Install** → **Open** 🎉

### Step 8 — Give the app its key

1. Open **Mark LV** on your phone
2. Press the ⚙️ **Settings** icon (top right)
3. Paste your Gemini key from Step 2 into the first box
4. (Optional) change the assistant name and your name
5. Press **Save**, go back, and press the big **microphone button**. Talk to it. It talks back.

**Every next time:** the app already works. To change anything in the app later, change the files on GitHub (Steps 4) — GitHub builds a **new** APK automatically with a new release, and you just download it again (Step 6).

---

## 🔧 Something broke? (the dot is red ❌)

- Press on the red ❌, then press **"Build Mark LV APK"**, then press on the failed step to read the error
- Most common cause: the `.github` folder was not uploaded (Step 4, number 3). Fix: press **Add file → Upload files** in the repo, and upload the `.github` folder again
- Second most common: you pressed upload but the files landed inside a folder-inside-a-folder. Fix: in your repo, make sure the FIRST thing you see is the folders `app` and `.github`, and files like `build.gradle.kts` — not a folder called `Mark-LV-Android`
- To try again: **Actions** tab → press the failed run → top right **"Re-run all jobs"**

---

## 📁 What is inside this folder (for the curious)

| File / folder | What it is |
|---|---|
| `.github/workflows/build-apk.yml` | The recipe GitHub follows to build the APK |
| `app/` | The Android app itself (Kotlin code + screens) |
| `app/src/main/java/com/marklv/assistant/` | The brain: Gemini chat, voice, phone skills, memory |
| `app/src/main/res/` | The face: icons, colors, texts |
| `build.gradle.kts` (2 files) | Shopping lists: which libraries the app needs |
| `settings.gradle.kts`, `gradle.properties` | Small settings for the builder |

---

# 🇦🇫 نسخهٔ دری — راهنمای خیلی ساده

**این چیست؟** یک پوشهٔ جادویی که GitHub آن را به یک اپ اندروید واقعی (فایل `.apk`) تبدیل می‌کند — رایگان، و شما هیچ کد‌نویسی لازم ندارید.

اپ چه کار می‌کند: با آن حرف می‌زنید، جواب می‌دهد و بلند حرف می‌زند؛ چراغ قوه را روشن و خاموش می‌کند؛ باتری را می‌گوید؛ اپ‌ها و سایت‌ها را باز می‌کند؛ در گوگل جستجو می‌کند؛ و چیزهایی دربارهٔ شما به یاد می‌سپارد. به زبان شما (دری/پشتو/فارسی/انگلیسی) جواب می‌دهد.

### گام ۱ — اکانت GitHub بسازید
۱. به **github.com** بروید. ۲. **Sign up** را بزنید. ۳. ایمیل و رمز بدهید. ۴. کدی که به ایمیل‌تان آمد را وارد کنید. تمام.

### گام ۲ — کلید رایگان Gemini بگیرید (پاسِ مغزِ اپ)
۱. در مرورگر **aistudio.google.com/apikey** را باز کنید. ۲. با اکانت گوگل داخل شوید. ۳. **Create API key** را بزنید. ۴. متن بلندی می‌آید — آن را **کاپی** کنید و جایی نگه دارید. در گام ۸ لازم می‌شود.

### گام ۳ — یک «ریپو» (صندوق) بسازید
۱. به github.com داخل شوید. ۲. علامت **+** بالا راست → **New repository**. ۳. نام: **`MarkLV-Android`**. ۴. **Public** را انتخاب کنید (مهم!). ۵. دکمهٔ سبز **Create repository** را بزنید.

### گام ۴ — فایل‌ها را داخل صندوق بریزید (آپلود)
۱. در صفحهٔ جدید روی لینک **"uploading an existing file"** بزنید. ۲. در کمپیوترتان پوشهٔ **`Mark-LV-Android`** را باز کنید، یک بار داخلش کلیک کنید، **Ctrl+A** بزنید (همه را انتخاب می‌کند)، و همه را با موس بکشید داخل کادر آپلود مرورگر. ۳. ⚠️ چک کنید که پوشهٔ **`.github`** در فهرست دیده می‌شود — اگر نیست، خودش را تنها دوباره بکشید داخل کادر (بدون این پوشه ساخت کار نمی‌کند). ۴. پایین بروید و دکمهٔ سبز **Commit changes** را بزنید.

### گام ۵ — تماشا کنید که GitHub اپ را می‌سازد
۱. بالای صفحه تب **Actions** را بزنید. ۲. نقطهٔ زرد 🟡 یعنی کار می‌کند. ۳. **۵ تا ۱۰ دقیقه** صبر کنید تا سبز ✅ شود.

### گام ۶ — اپ را دانلود کنید
۱. در صفحهٔ ریپو، سمت راست روی **Releases** بزنید. ۲. بالاترین نسخه را باز کنید. ۳. زیر "Assets" روی **MarkLV.apk** بزنید. 💡 آسان‌تر: همین صفحه را با **مرورگر گوشی** باز کنید — فایل مستقیم در گوشی دانلود می‌شود.

### گام ۷ — روی گوشی نصب کنید
۱. در گوشی: **Files → Downloads → MarkLV.apk** را لمس کنید. ۲. گوشی می‌گوید اجازه نیست — طبیعی است! روی **Settings** همان پیام بزنید و **"Allow from this source"** را روشن کنید، برگردید. ۳. **Install** → **Open**. 🎉

### گام ۸ — کلید را به اپ بدهید
۱. اپ **Mark LV** را باز کنید. ۲. علامت ⚙️ بالا راست را بزنید. ۳. کلید Gemini (گام ۲) را در کادر اول بچسبانید. ۴. **Save** بزنید، برگردید، و دکمهٔ بزرگ **میکروفون** را لمس کنید و حرف بزنید.

### اگر نقطه سرخ شد ❌
تب **Actions** → روی ساختِ سرخ بزنید → ببینید کدام قدم خطا دارد. رایج‌ترین دلیل: پوشهٔ `.github` آپلود نشده — با **Add file → Upload files** دوباره آپلودش کنید. برای تلاش دوباره: **Re-run all jobs**.

### دفعه‌های بعد
هر وقت فایلی را در GitHub عوض کنید، GitHub خودش یک APK **تازه** می‌سازد و در Releases می‌گذارد — فقط دوباره دانلود و نصب کنید. همین.

---

*Based on MARK LV (55) by FatihMakes — the Android edition keeps the voice, the memory and the model ladder, and swaps computer-control for phone-control.*
