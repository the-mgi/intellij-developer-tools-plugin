package dev.turingcomplete.intellijdevelopertoolsplugins._internal.tool.generator.uuid

import com.fasterxml.uuid.Generators
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.whenItemSelectedFromUi
import com.intellij.ui.layout.ComboBoxPredicate
import dev.turingcomplete.intellijdevelopertoolsplugins.DeveloperTool
import dev.turingcomplete.intellijdevelopertoolsplugins.DeveloperToolConfiguration
import dev.turingcomplete.intellijdevelopertoolsplugins.DeveloperToolFactory
import dev.turingcomplete.intellijdevelopertoolsplugins.DeveloperToolPresentation
import dev.turingcomplete.intellijdevelopertoolsplugins._internal.common.toMessageDigest
import dev.turingcomplete.intellijdevelopertoolsplugins._internal.tool.generator.OneLineTextGenerator

internal class UuidGenerator(configuration: DeveloperToolConfiguration, parentDisposable: Disposable) :
  OneLineTextGenerator(
    presentation = DeveloperToolPresentation("UUID", "UUID Generator"),
    configuration = configuration,
    parentDisposable = parentDisposable
  ) {
  // -- Properties -------------------------------------------------------------------------------------------------- //

  private var selectedVersion by configuration.register("version", UuidVersion.V4)

  private val uuidV1Generator by lazy { UuidV1Generator(configuration, parentDisposable) }
  private val uuidV3Generator by lazy { UuidV3Generator(configuration, parentDisposable) }
  private val uuidV4Generator by lazy { UuidV4Generator() }
  private val uuidV5Generator by lazy { UuidV5Generator(configuration, parentDisposable) }
  private val uuidV6Generator by lazy { UuidV6Generator(configuration, parentDisposable) }
  private val uuidV7Generator by lazy { UuidV7Generator() }

  // -- Initialization ---------------------------------------------------------------------------------------------- //
  // -- Exposed Methods --------------------------------------------------------------------------------------------- //

  @Suppress("UnstableApiUsage")
  override fun Panel.buildConfigurationUi() {
    lateinit var selectedVersionComboBox: ComboBox<UuidVersion>
    row {
      selectedVersionComboBox = comboBox(UuidVersion.values().toList())
              .label("Version:")
              .applyToComponent {
                selectedItem = selectedVersion
              }
              .whenItemSelectedFromUi(parentDisposable) {
                selectedVersion = it
                handleVersionSelection(selectedVersion)
                generatedTextTitle
              }.component
    }

    with(uuidV1Generator) {
      buildConfigurationUi(ComboBoxPredicate(selectedVersionComboBox) { it == UuidVersion.V1 })
    }
    with(uuidV3Generator) {
      buildConfigurationUi(ComboBoxPredicate(selectedVersionComboBox) { it == UuidVersion.V3 })
    }
    with(uuidV4Generator) {
      buildConfigurationUi(ComboBoxPredicate(selectedVersionComboBox) { it == UuidVersion.V4 })
    }
    with(uuidV5Generator) {
      buildConfigurationUi(ComboBoxPredicate(selectedVersionComboBox) { it == UuidVersion.V5 })
    }
    with(uuidV6Generator) {
      buildConfigurationUi(ComboBoxPredicate(selectedVersionComboBox) { it == UuidVersion.V6 })
    }
    with(uuidV7Generator) {
      buildConfigurationUi(ComboBoxPredicate(selectedVersionComboBox) { it == UuidVersion.V7 })
    }

    handleVersionSelection(selectedVersion)
  }

  override fun generate(): String = getSpecificGenerator(selectedVersion).generate()

  // -- Private Methods --------------------------------------------------------------------------------------------- //

  private fun handleVersionSelection(version: UuidVersion) {
    generatedTextTitle.set("Generated ${version.title}:")
    supportsBulkGeneration.value = getSpecificGenerator(version).supportsBulkGeneration
  }

  private fun getSpecificGenerator(version: UuidVersion): SpecificUuidGenerator = when (version) {
    UuidVersion.V1 -> uuidV1Generator
    UuidVersion.V3 -> uuidV3Generator
    UuidVersion.V4 -> uuidV4Generator
    UuidVersion.V5 -> uuidV5Generator
    UuidVersion.V6 -> uuidV6Generator
    UuidVersion.V7 -> uuidV7Generator
  }

  // -- Inner Type -------------------------------------------------------------------------------------------------- //

  private class UuidV1Generator(
    configuration: DeveloperToolConfiguration,
    parentDisposable: Disposable
  ) : MacAddressBasedUuidGenerator(UuidVersion.V1, configuration, parentDisposable, true) {

    override fun generate(): String = Generators.timeBasedGenerator(getEthernetAddress()).generate().toString()
  }

  // -- Inner Type -------------------------------------------------------------------------------------------------- //

  private class UuidV3Generator(
    configuration: DeveloperToolConfiguration,
    parentDisposable: Disposable
  ) : NamespaceAndNameBasedUuidGenerator(UuidVersion.V3, "MD5".toMessageDigest(), configuration, parentDisposable, false)

  // -- Inner Type -------------------------------------------------------------------------------------------------- //

  private class UuidV4Generator : SpecificUuidGenerator(true) {

    override fun generate(): String = Generators.randomBasedGenerator().generate().toString()
  }

  // -- Inner Type -------------------------------------------------------------------------------------------------- //

  private class UuidV5Generator(
    configuration: DeveloperToolConfiguration,
    parentDisposable: Disposable
  ) : NamespaceAndNameBasedUuidGenerator(UuidVersion.V5, "SHA-1".toMessageDigest(), configuration, parentDisposable, false)

  // -- Inner Type -------------------------------------------------------------------------------------------------- //

  private class UuidV6Generator(
    configuration: DeveloperToolConfiguration,
    parentDisposable: Disposable
  ) : MacAddressBasedUuidGenerator(UuidVersion.V6, configuration, parentDisposable, true) {

    override fun generate(): String = Generators.timeBasedReorderedGenerator(getEthernetAddress()).generate().toString()
  }

  // -- Inner Type -------------------------------------------------------------------------------------------------- //

  private class UuidV7Generator : SpecificUuidGenerator(true) {

    override fun generate(): String = Generators.timeBasedEpochGenerator().generate().toString()
  }

  // -- Inner Type -------------------------------------------------------------------------------------------------- //

  class Factory : DeveloperToolFactory {

    override fun createDeveloperTool(configuration: DeveloperToolConfiguration, project: Project?, parentDisposable: Disposable): DeveloperTool {
      return UuidGenerator(configuration, parentDisposable)
    }
  }

  // -- Companion Object -------------------------------------------------------------------------------------------- //
}