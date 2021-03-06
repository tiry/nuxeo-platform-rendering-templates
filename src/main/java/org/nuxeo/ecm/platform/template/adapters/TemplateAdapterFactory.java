/*
 * (C) Copyright 2006-20012 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 */

package org.nuxeo.ecm.platform.template.adapters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;
import org.nuxeo.ecm.platform.template.adapters.doc.TemplateBasedDocument;
import org.nuxeo.ecm.platform.template.adapters.doc.TemplateBasedDocumentAdapterImpl;
import org.nuxeo.ecm.platform.template.adapters.source.TemplateSourceDocument;
import org.nuxeo.ecm.platform.template.adapters.source.TemplateSourceDocumentAdapterImpl;

/**
 * Pluggable {@link DocumentAdapterFactory} used to return the right
 * {@link TemplateBasedDocument} or {@link TemplateSourceDocument}
 * implementation according to given {@link DocumentModel}.
 * 
 * @author Tiry (tdelprat@nuxeo.com)
 * 
 */
public class TemplateAdapterFactory implements DocumentAdapterFactory {

    protected static final Log log = LogFactory.getLog(TemplateAdapterFactory.class);

    @SuppressWarnings("rawtypes")
    public Object getAdapter(DocumentModel doc, Class adapterClass) {

        if (adapterClass.getSimpleName().equals(
                TemplateBasedDocument.class.getSimpleName())) {
            if (doc.hasFacet(TemplateBasedDocumentAdapterImpl.TEMPLATEBASED_FACET)) {
                try {
                    return new TemplateBasedDocumentAdapterImpl(doc);
                } catch (ClientException e) {
                    log.error(
                            "Unable to create TemplateBasedDocumentAdapterImpl",
                            e);
                    return null;
                }
            } else {
                return null;
            }
        }

        if (adapterClass.getSimpleName().equals(
                TemplateSourceDocument.class.getSimpleName())) {
            if (doc.hasFacet(TemplateSourceDocumentAdapterImpl.TEMPLATE_FACET)) {
                return new TemplateSourceDocumentAdapterImpl(doc);
            } else {
                return null;
            }
        }

        return null;
    }
}
