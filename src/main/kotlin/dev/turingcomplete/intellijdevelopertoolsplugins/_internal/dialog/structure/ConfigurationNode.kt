package dev.turingcomplete.intellijdevelopertoolsplugins._internal.dialog.structure

import com.intellij.icons.AllIcons

internal class ConfigurationNode : ContentNode(
  id = "configuration",
  title = "Configuration",
  weight = Int.MAX_VALUE,
  icon = AllIcons.General.Gear,
  isSecondaryNode = true
) {
  // -- Properties -------------------------------------------------------------------------------------------------- //
  // -- Initialization ---------------------------------------------------------------------------------------------- //
  // -- Exposed Methods --------------------------------------------------------------------------------------------- //
  // -- Private Methods --------------------------------------------------------------------------------------------- //
  // -- Inner Type -------------------------------------------------------------------------------------------------- //
  // -- Companion Object -------------------------------------------------------------------------------------------- //
}