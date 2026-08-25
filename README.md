# Ramesh AI — Android Assistant App (Starter Project)

This is a working **Android Studio project skeleton** (Kotlin + Jetpack Compose)
for the "Ramesh AI" assistant described in the spec. It's a real, buildable
starting point — not the entire 21-feature product. Below is exactly what's
implemented, what's stubbed, and what you still need to build.

## How to open it
1. Open the `RameshAI/` folder in Android Studio (Koala/2024.1+ recommended).
2. Let Gradle sync (it will pull Compose, Retrofit, Room, DataStore, Coil).
3. Run on a device/emulator with API 26+.

## What's actually implemented
- **Clean module layout**: `data/`, `model/`, `network/`, `voice/`, `ui/` as in the spec's architecture diagram.
- **Chat UI**: dark purple/blue theme, rounded bubbles, mode selector (Chat/Coding/Study/Creative), quick-action home state, typing indicator, per-message copy/edit/regenerate/share/delete actions.
- **Voice**: `VoiceManager` wraps Android's built-in `SpeechRecognizer` (STT, supports `hi-IN`/`en-IN` language tags) and `TextToSpeech` (TTS), with Idle/Listening/Thinking/Speaking states, an animated orb, interrupt-to-stop, and a dedicated voice-first screen.
- **Networking**: a `RameshApiService` (Retrofit) that talks **only to your own backend** — `POST /v1/chat`, `/v1/search`, `/v1/vision`, `/v1/document`. No provider API key ever ships in the app (network security config blocks cleartext; keys live server-side).
- **Honesty guarantees baked into code**: `ChatMessage.usedWebSearch`/`sources` are only ever populated from what the backend reports — the UI has no path to fabricate a "searched the web" claim. When no backend is configured yet, the app runs in an explicit **demo mode** and labels every reply `[Demo mode]` instead of pretending to be a real AI.
- **Settings screen**: assistant name, user name, theme, auto-voice-reply, save-chats, web-search toggle, memory toggle, speech rate.

## What's intentionally stubbed (needs your backend)
The Android app is a client. The actual intelligence — the AI orchestrator,
web search, vision, and document parsing — must live on a server you control
(section 18 of the spec). This starter's `NetworkModule.isDemoMode` flag
flips to `false` the moment you set a real `BACKEND_BASE_URL` in
`app/build.gradle.kts`, and the four endpoints in `RameshApiService.kt` are
exactly what your backend needs to implement.

A minimal backend orchestrator just needs to, per request:
1. Look at the user's message + mode.
2. Decide: plain AI answer vs. web search vs. vision vs. document parsing.
3. Call the relevant provider(s) with your server-side keys.
4. Return `{ reply, usedWebSearch, sources }` — never set `usedWebSearch: true` unless a search actually ran.

## Not yet built (roadmap)
These are real, scoped follow-ups rather than one-shot additions:
- **Room database wiring** for persistent, searchable chat history, pin/favorite/rename/delete (models exist in `model/Models.kt`; DAOs/entities not yet added).
- **Image tools**: camera/gallery capture, crop/rotate/brightness/contrast/grayscale editor, and calling `/v1/vision` for description/OCR/object detection.
- **Document attach + summarize** flow calling `/v1/document` (PDF/DOCX/TXT).
- **Offline queueing** of messages sent with no connectivity.
- **Chat history sidebar/drawer**, search-within-chats.
- **Onboarding, About, Privacy, Feedback screens**.
- **Local on-device model fallback** for offline conversations (would use MediaPipe LLM Inference or similar — meaningfully changes app size/perf, worth its own decision).
- Real launcher icon assets (currently references `@mipmap/ic_launcher`, which you'll generate via Android Studio's Image Asset tool).

## Why the scope is cut this way
Sections 5, 6, 7, and 18 of the spec (web search, image AI, document AI,
orchestration) all require a real backend with real provider credentials —
that can't be safely or honestly stubbed inside the APK. Rather than fake
those with hardcoded "AI-sounding" responses (which the spec explicitly
forbids — "never pretend a search happened," "don't claim AI editing
happened unless it did"), this starter makes the seam explicit and labeled,
so nothing here misrepresents itself as more capable than it is.

## Suggested next step
Tell me which single feature area to build out next in depth — most people
building this find it easiest to go: **backend orchestrator (Node/Python) →
Room persistence → image tools → document tools**, in that order.
