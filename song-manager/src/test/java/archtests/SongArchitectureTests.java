package archtests;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

public class SongArchitectureTests {

    private JavaClasses classes;

    @BeforeEach
    void setup() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.example.songs");
    }

    @Test
    void shouldFollowLayeredArchitecture() {
        layeredArchitecture()
                .consideringAllDependencies()
                .layer("Controller").definedBy("..controller..")
                .layer("Service").definedBy("..service..")
                .layer("Repository").definedBy("..repository..")
                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
                .check(classes);
    }

    @Test
    void controllersShouldNotDependOnOtherControllers() {
        noClasses().that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .check(classes);
    }

    @Test
    void servicesShouldNotDependOnControllers() {
        noClasses().that().resideInAPackage("..service..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .check(classes);
    }

    @Test
    void repositoriesShouldNotDependOnServices() {
        noClasses().that().resideInAPackage("..repository..")
                .should().dependOnClassesThat().resideInAPackage("..service..")
                .check(classes);
    }

    @Test
    void modelsShouldNotDependOnServiceOrController() {
        noClasses().that().resideInAPackage("..model..")
                .should().dependOnClassesThat().resideInAnyPackage("..service..", "..controller..")
                .check(classes);
    }

    @Test
    void controllerClassesShouldBeAnnotatedWithRestController() {
        classes().that().resideInAPackage("..controller..")
                .should().beAnnotatedWith(RestController.class)
                .check(classes);
    }

    @Test
    void controllerClassNamesShouldEndWithController() {
        classes().that().resideInAPackage("..controller..")
                .should().haveSimpleNameEndingWith("Controller")
                .check(classes);
    }

    @Test
    void serviceClassNamesShouldEndWithService() {
        classes().that().resideInAPackage("..service..")
                .should().haveSimpleNameEndingWith("Service")
                .check(classes);
    }

    @Test
    void repositoryClassNamesShouldEndWithRepository() {
        classes().that().resideInAPackage("..repository..")
                .should().haveSimpleNameEndingWith("Repository")
                .check(classes);
    }

    @Test
    void repositoryShouldBeInterfaces() {
        classes().that().resideInAPackage("..repository..")
                .should().beInterfaces()
                .check(classes);
    }

    @Test
    void serviceClassesShouldBeAnnotatedWithService() {
        classes().that().resideInAPackage("..service..")
                .should().beAnnotatedWith(org.springframework.stereotype.Service.class)
                .check(classes);
    }

    @Test
    void fieldsInModelShouldBePrivate() {
        fields().that().areDeclaredInClassesThat().resideInAPackage("..model..")
                .should().bePrivate()
                .check(classes);
    }

    @Test
    void controllerFieldsShouldNotBeAutowired() {
        noFields().that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .should().beAnnotatedWith(Autowired.class)
                .check(classes);
    }

    @Test
    void serviceFieldsShouldNotBeAutowired() {
        noFields().that().areDeclaredInClassesThat().resideInAPackage("..service..")
                .should().beAnnotatedWith(Autowired.class)
                .check(classes);
    }

    @Test
    void noFieldInjectionAllowedAnywhere() {
        noFields().should().beAnnotatedWith(Autowired.class).check(classes);
    }

    @Test
    void modelClassesShouldBePublic() {
        classes().that().resideInAPackage("..model..")
                .should().bePublic()
                .check(classes);
    }

    @Test
    void serviceShouldOnlyDependOnModelAndRepository() {
        classes().that().resideInAPackage("..service..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage("..model..", "..repository..", "..service..", "java..", "javax..", "org.springframework..")
                .check(classes);
    }

    @Test
    void modelClassesShouldNotBeAnnotatedWithAutowired() {
        noClasses().that().resideInAPackage("..model..")
                .should().beAnnotatedWith(Autowired.class)
                .check(classes);
    }

    @Test
    void controllerShouldOnlyDependOnServiceAndModel() {
        classes().that().resideInAPackage("..controller..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage("..model..", "..service..", "java..", "javax..", "org.springframework..")
                .check(classes);
    }

    @Test
    void allClassesShouldBeInAppropriatePackages() {
        // Allow SongManagerApplication to reside in the base package (songs)
        classes().should().resideInAnyPackage("..controller..", "..service..", "..repository..", "..model..", "com.example.songs")
                .check(classes);
    }
}
