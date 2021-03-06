package org.nuxeo.ecm.platform.template.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.platform.template.adapters.source.TemplateSourceDocument;
import org.nuxeo.ecm.platform.template.adapters.source.TemplateSourceDocumentAdapterImpl;
import org.nuxeo.runtime.api.Framework;

public class TemplateMappingFetcher extends UnrestrictedSessionRunner {

    protected static String repoName;

    protected static final Log log = LogFactory.getLog(TemplateMappingFetcher.class);

    protected static String getRepoName() {
        if (repoName == null) {
            RepositoryManager rm = Framework.getLocalService(RepositoryManager.class);
            repoName = rm.getDefaultRepository().getName();
        }
        return repoName;
    }

    protected Map<String, String> mapping = new HashMap<String, String>();

    protected TemplateMappingFetcher() {
        super(getRepoName());
    }

    @Override
    public void run() throws ClientException {
        StringBuffer sb = new StringBuffer("select * from Document where ");
        sb.append(TemplateSourceDocumentAdapterImpl.TEMPLATE_FORCED_TYPES_PROP);
        sb.append(" <> ''");
        sb.append(" order by dc:modified desc");

        DocumentModelList docs = session.query(sb.toString());

        for (DocumentModel doc : docs) {
            TemplateSourceDocument tmpl = doc.getAdapter(TemplateSourceDocument.class);
            if (tmpl!=null) {
                for (String type : tmpl.getForcedTypes()) {
                    if (mapping.containsKey(type)) {
                        log.warn("Several templates are mapped to type " + type + ": " + mapping.get(type) + " -- " + doc.getId());
                        // XXX fix
                    } else {
                        mapping.put(type, doc.getId());
                    }
                }
            }
        }
    }

    public Map<String, String> getMapping() {
        return mapping;
    }
}
