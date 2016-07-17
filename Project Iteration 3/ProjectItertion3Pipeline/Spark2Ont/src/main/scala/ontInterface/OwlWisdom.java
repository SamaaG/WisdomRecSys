package ontInterface;

/**
 * Created by Samaa on 7/15/2016.
 */

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class OwlWisdom {
    OWLOntology ont;
    PrefixManager pm;
    OWLOntologyManager manager;
    OWLDataFactory df;

    public OwlWisdom() {
        try {
            pm = new DefaultPrefixManager(null, null, "https://www.sama.com/OWL/tutorial#");
            manager = OWLManager.createOWLOntologyManager();
            df = manager.getOWLDataFactory();
            ont = initialzation();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void createSubClass(String className, String subClassName) {
        OWLClass baseClass = df.getOWLClass(className, pm);
        OWLClass subClass = df.getOWLClass(subClassName, pm);
        OWLSubClassOfAxiom declarationSubClassAxiom = df.getOWLSubClassOfAxiom(subClass, baseClass);
        manager.addAxiom(ont, declarationSubClassAxiom);
    }

    public void createClass(String className) {

        OWLClass classN = df.getOWLClass(className, pm);
        OWLDeclarationAxiom declarationAxiom = df.getOWLDeclarationAxiom(classN);
        manager.addAxiom(ont, declarationAxiom);

    }

    public void createIndividual(String individualName, String className) {
        OWLClass classN = df.getOWLClass(className, pm);
        OWLNamedIndividual ind = df.getOWLNamedIndividual(individualName, pm);
        OWLClassAssertionAxiom classAssertion = df.getOWLClassAssertionAxiom(classN, ind);
        manager.addAxiom(ont, classAssertion);

    }

    public void createObjectProperty(String domain, String propertyName, String range) {

        OWLClass domainC = df.getOWLClass(domain, pm);
        OWLClass rangeC = df.getOWLClass(range, pm);
        OWLObjectProperty isIngredientOf = df.getOWLObjectProperty(propertyName, pm);
        OWLObjectPropertyRangeAxiom rangeAxiom = df.getOWLObjectPropertyRangeAxiom(isIngredientOf, rangeC);
        OWLObjectPropertyDomainAxiom domainAxiom = df.getOWLObjectPropertyDomainAxiom(isIngredientOf, domainC);
        manager.addAxiom(ont, rangeAxiom);
        manager.addAxiom(ont, domainAxiom);

    }

    public OWLOntology initialzation() throws Exception {
        //creating ontology manager
        //In order to create objects that represent entities we need a

        ont = manager.createOntology(IRI.create("https://www.sama.com/OWL/", "tutorial"));
        return ont;
    }

    public void saveOntology() {
        try {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OutputStream os = new FileOutputStream("data/OwlWisdonRecSys.owl");
            OWLXMLDocumentFormat owlxmlFormat = new OWLXMLDocumentFormat();
            manager.saveOntology(ont, owlxmlFormat, os);
            System.out.println("Ontology Created");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}