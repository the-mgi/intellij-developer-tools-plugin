package dev.turingcomplete.intellijdevelopertoolsplugin._internal.tool

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAwareAction
import dev.turingcomplete.intellijdevelopertoolsplugin.DeveloperUiToolFactory
import dev.turingcomplete.intellijdevelopertoolsplugin.DeveloperUiToolPresentation
import dev.turingcomplete.intellijdevelopertoolsplugin._internal.DeveloperUiToolFactoryEp
import dev.turingcomplete.intellijdevelopertoolsplugin._internal.settings.DeveloperToolsApplicationSettings
import dev.turingcomplete.intellijdevelopertoolsplugin._internal.ui.instance.handling.OpenDeveloperToolService

object ShowDeveloperToolUtils {
  // -- Properties -------------------------------------------------------------------------------------------------- //

  val showDeveloperToolActions: Array<AnAction> by lazy { createShowDeveloperToolsActions() }

  // -- Initialization ---------------------------------------------------------------------------------------------- //
  // -- Exported Methods -------------------------------------------------------------------------------------------- //
  // -- Private Methods --------------------------------------------------------------------------------------------- //

  private fun createShowDeveloperToolsActions(): Array<AnAction> {
    val showDeveloperToolActions = mutableListOf<AnAction>()

    val application = ApplicationManager.getApplication()
    val showInternalTools = DeveloperToolsApplicationSettings.instance.showInternalTools
    DeveloperUiToolFactoryEp.EP_NAME.forEachExtensionSafe { developerToolFactoryEp ->
      if (developerToolFactoryEp.internalTool && !showInternalTools) {
        return@forEachExtensionSafe
      }

      val developerUiToolFactory: DeveloperUiToolFactory<*> = developerToolFactoryEp.createInstance(application)
      val showToolAction = createShowToolAction(
        id = developerToolFactoryEp.id,
        presentation = developerUiToolFactory.getDeveloperUiToolPresentation()
      )
      showDeveloperToolActions.add(showToolAction)

      ActionManager.getInstance().registerAction("show-developer-tool-${developerToolFactoryEp.id}", showToolAction)
    }

    return showDeveloperToolActions.sortedBy { it.templateText }.toTypedArray()
  }

  private fun createShowToolAction(id: String, presentation: DeveloperUiToolPresentation) =
    object : DumbAwareAction(presentation.contentTitle) {

      override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
      }

      override fun getActionUpdateThread() = ActionUpdateThread.BGT

      override fun actionPerformed(e: AnActionEvent) {
        e.project?.service<OpenDeveloperToolService>()?.showTool(id)
      }
    }

  // -- Inner Type -------------------------------------------------------------------------------------------------- //
}