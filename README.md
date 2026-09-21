# General Surgery Scheduler — PWA

هذه الحزمة جاهزة للرفع إلى GitHub Pages.

## الملفات
- `index.html` — التطبيق.
- `manifest.webmanifest` — إعدادات PWA.
- `sw.js` — العمل دون اتصال والتحديثات.
- `icons/` — أيقونات Android/iPhone.
- `offline.html` — صفحة احتياطية دون اتصال.
- `.nojekyll` — لتقديم الملفات كما هي على GitHub Pages.

## النشر على GitHub Pages
1. أنشئ مستودعًا جديدًا، مثل `surgery-scheduler`.
2. ارفع **محتويات هذه الحزمة إلى جذر المستودع**.
3. من GitHub افتح: `Settings → Pages`.
4. اختر `Deploy from a branch`.
5. Branch: `main`، Folder: `/ (root)`.
6. بعد النشر افتح رابط GitHub Pages من الجوال.

## التثبيت
### Android
افتح الرابط في Chrome ثم اختر Install app / Add to Home screen.
يمكن أيضًا أن يظهر زر «تثبيت التطبيق» داخل التطبيق إذا سمح المتصفح.

### iPhone
افتح الرابط في Safari → Share → Add to Home Screen.

## مشاركة نتيجة ChatGPT
- الطريقة المشتركة في Android وiPhone: احفظ ملف `Surgery_Chat_Import_YYYY_MM.txt` ثم استورده من صفحة البيانات.
- على Android، بعد تثبيت الـPWA، تتضمن الحزمة أيضًا `share_target` لاستقبال ملف `.txt/.json` من قائمة المشاركة عندما يدعم النظام ذلك.
- iPhone يعتمد على اختيار الملف من تطبيق Files.

## التخزين
بيانات التطبيق تبقى محليًا داخل المتصفح/الـPWA على الجهاز. النسخة الاحتياطية داخل التطبيق هي الطريقة الموصى بها لنقل إعداداتك إلى جهاز آخر.
