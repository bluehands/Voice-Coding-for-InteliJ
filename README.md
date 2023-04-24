# VoiceToAutoCode
A plugin for JetBrains Rider that transcribes voice input into program code.
The recoginition is powered by the autocomplete feature and Microsoft Azure Voice recognition.
A Microsoft Azure Voice subscription is required to use this plugin.
This plugin is based on the template for rider plugins: https://github.com/jetbrains/resharper-rider-plugin/

# Setup
The plugin can be installed via the jetbrains store.
After startup you first need set the azure subscription credentials.
For better results go to settings, set a working directory and select "Record via Buffer File".

# About the Plugin
This plugin uses the powerful autocomplete features of the intellij-platform to implement a accurate voice-to-code transcription.
It uses the autocomplete feature to generate a dynamic list of probable code for any given context.
Based on this list the speech input is processed and a coding input is generated.
It offers a few extra features to improve those results with homophones and matching algorithms, which can be turned on in the options.
The current version is developed for Rider, but future versions are planned support all IDEs based on the intellij platform.
