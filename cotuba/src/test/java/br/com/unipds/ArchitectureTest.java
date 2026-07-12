package br.com.unipds;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.jmolecules.archunit.JMoleculesArchitectureRules;
import org.jmolecules.archunit.JMoleculesDddRules;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "br.com.unipds")
class ArchitectureTest {

    @ArchTest
    static final ArchRule dddRules = JMoleculesDddRules.all();

    @ArchTest
    static final ArchRule layering = JMoleculesArchitectureRules.ensureLayering();

    @ArchTest
    static final ArchRule noCycles =
            slices().matching("br.com.unipds.(*)..").should().beFreeOfCycles();
}
