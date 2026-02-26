# FocusDelay (V1 MVP)

## Folder structure

```text
app/src/main/java/com/focusdelay/
  data/
    AppInfo.kt
    AppRepository.kt
    PrefsManager.kt
  service/
    FocusAccessibilityService.kt
  ui/
    MainActivity.kt
    AppSelectionActivity.kt
    OverlayActivity.kt
  utils/
    PermissionNavigator.kt
  viewmodel/
    MainViewModel.kt
    AppSelectionViewModel.kt
```

## Integration order
1. Create base Android + Compose project (minSdk 26) and add Material3/ViewModel dependencies.
2. Add `PrefsManager` and app listing repository.
3. Build `MainActivity` and `AppSelectionActivity` with viewmodels.
4. Add `OverlayActivity` countdown UI.
5. Register `FocusAccessibilityService` and accessibility config XML.
6. Wire permission buttons to Usage Access, Accessibility, and Overlay settings.
7. Test on real device: grant permissions, select apps, verify overlay delay.
