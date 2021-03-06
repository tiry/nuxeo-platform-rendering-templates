package org.nuxeo.ecm.webapp.templates;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.In;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.rendition.service.RenditionDefinition;
import org.nuxeo.ecm.platform.rendition.service.RenditionService;
import org.nuxeo.ecm.platform.template.adapters.doc.TemplateBasedDocument;
import org.nuxeo.ecm.platform.template.adapters.source.TemplateSourceDocument;
import org.nuxeo.ecm.platform.template.rendition.TemplateBasedRenditionProvider;
import org.nuxeo.ecm.platform.template.service.TemplateProcessorService;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.runtime.api.Framework;

public class BaseTemplateAction implements Serializable {

    private static final long serialVersionUID = 1L;

    protected static final Log log = LogFactory.getLog(BaseTemplateAction.class);

    @In(create = true)
    protected transient NavigationContext navigationContext;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    public boolean canAddTemplateInputs() {
        DocumentModel currentDocument = navigationContext.getCurrentDocument();
        TemplateSourceDocument template = currentDocument.getAdapter(TemplateSourceDocument.class);
        return template != null ? true : false;
    }

    public boolean canUpdateTemplateInputs(String templateName)
            throws ClientException {
        DocumentModel currentDocument = navigationContext.getCurrentDocument();
        TemplateSourceDocument template = currentDocument.getAdapter(TemplateSourceDocument.class);
        if (template != null) {
            return true;
        }
        TemplateBasedDocument templateBased = currentDocument.getAdapter(TemplateBasedDocument.class);
        if (templateBased != null) {
            return templateBased.hasEditableParams(templateName)
                    && documentManager.hasPermission(currentDocument.getRef(),
                            "Write");
        }
        return false;
    }

    public boolean canResetParameters() throws ClientException {
        DocumentModel currentDocument = navigationContext.getCurrentDocument();
        TemplateBasedDocument templateBased = currentDocument.getAdapter(TemplateBasedDocument.class);
        if (templateBased != null) {
            return true;
        }
        return false;
    }

    public TemplateSourceDocument getCurrentDocumentAsTemplateSourceDocument() {
        return navigationContext.getCurrentDocument().getAdapter(
                TemplateSourceDocument.class);
    }

    public DocumentModel resolveTemplateById(String uuid) {
        try {
            return documentManager.getDocument(new IdRef(uuid));
        } catch (Exception e) {
            return null;
        }
    }

    public List<RenditionDefinition> getRenditions() {
        RenditionService rs = Framework.getLocalService(RenditionService.class);
        return rs.getDeclaredRenditionDefinitionsForProviderType(TemplateBasedRenditionProvider.class.getSimpleName());
    }

    public List<TemplateSourceDocument> getAvailableOfficeTemplates(
            String targetType) throws ClientException {
        TemplateProcessorService tps = Framework.getLocalService(TemplateProcessorService.class);
        return tps.getAvailableOfficeTemplates(documentManager, targetType);
    }

}
