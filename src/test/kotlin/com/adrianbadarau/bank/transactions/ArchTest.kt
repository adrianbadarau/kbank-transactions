package com.adrianbadarau.bank.transactions

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchTest {

    @Test
    fun servicesAndRepositoriesShouldNotDependOnWebLayer() {

        val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.adrianbadarau.bank.transactions")

        noClasses()
            .that()
                .resideInAnyPackage("com.adrianbadarau.bank.transactions.service..")
            .or()
                .resideInAnyPackage("com.adrianbadarau.bank.transactions.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..com.adrianbadarau.bank.transactions.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses)
    }
}
