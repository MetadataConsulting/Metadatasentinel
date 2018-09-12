package uk.co.metadataconsulting.monitor;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.builder.NodeFactory;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class DlrValidator {
    private static final Logger log = LoggerFactory.getLogger(DlrValidator.class);

    public String validate(Map<String, String> identiferToValue, String rule) {

        try {
            KieBase kbase = loadKnowledgeBaseFromString(rule);
            KieSession ksession = kbase.newKieSession();

            List<String> list = new ArrayList();
            ksession.setGlobal( "output", list );
            if ( identiferToValue != null ) {
                for ( String identifier : identiferToValue.keySet() ) {
                    ksession.setGlobal( identifier, identiferToValue.get(identifier));
                }
            }
            ksession.fireAllRules();

            if ( list.isEmpty() ) {
                return null;
            }

            return list.stream().reduce((a, b) -> a + ", " + b).get();

        } catch (RuntimeException e) {
            log.error("Run time exception validating rule {}", e.getMessage());
            return null;
        }
    }

    protected KieBase loadKnowledgeBaseFromString(String... drlContentStrings) {
        return loadKnowledgeBaseFromString(null, null, drlContentStrings);
    }

    protected KieBase loadKnowledgeBaseFromString(KnowledgeBuilderConfiguration config, KieBaseConfiguration kBaseConfig, String... drlContentStrings) {
        return loadKnowledgeBaseFromString( config, kBaseConfig, (NodeFactory)null, drlContentStrings);
    }

    protected KieBase loadKnowledgeBaseFromString( KnowledgeBuilderConfiguration config, KieBaseConfiguration kBaseConfig, NodeFactory nodeFactory, String... drlContentStrings) {
        KnowledgeBuilder kbuilder = config == null ? KnowledgeBuilderFactory.newKnowledgeBuilder() : KnowledgeBuilderFactory.newKnowledgeBuilder(config);
        for (String drlContentString : drlContentStrings) {
            kbuilder.add(ResourceFactory.newByteArrayResource(drlContentString
                    .getBytes()), ResourceType.DRL);
        }

        if (kbuilder.hasErrors()) {
            //log.error(kbuilder.getErrors().toString());
        }
        if (kBaseConfig == null) {
            kBaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        }
        InternalKnowledgeBase kbase = kBaseConfig == null ? KnowledgeBaseFactory.newKnowledgeBase() : KnowledgeBaseFactory.newKnowledgeBase(kBaseConfig);
        if (nodeFactory != null) {
            ((KnowledgeBaseImpl) kbase).getConfiguration().getComponentFactory().setNodeFactoryProvider(nodeFactory);
        }
        kbase.addPackages( kbuilder.getKnowledgePackages());
        return kbase;
    }
}
