package org.nuxeo.ecm.platform.template.processors.jxls;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.jxls.transformer.XLSTransformer;

import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.platform.template.TemplateInput;
import org.nuxeo.ecm.platform.template.adapters.doc.TemplateBasedDocument;
import org.nuxeo.ecm.platform.template.fm.FMContextBuilder;
import org.nuxeo.ecm.platform.template.processors.AbstractTemplateProcessor;
import org.nuxeo.runtime.api.Framework;

public class JXLSTemplateProcessor extends AbstractTemplateProcessor {

    public static final String TEMPLATE_TYPE = "JXLS";

    @Override
    public Blob renderTemplate(TemplateBasedDocument templateBasedDocument,
            String templateName) throws Exception {

        Blob sourceTemplateBlob = getSourceTemplateBlob(templateBasedDocument,
                templateName);
        List<TemplateInput> params = templateBasedDocument.getParams(templateName);

        DocumentModel doc = templateBasedDocument.getAdaptedDoc();
        Map<String, Object> ctx = FMContextBuilder.build(doc, false);

        JXLSBindingResolver resolver = new JXLSBindingResolver();

        resolver.resolve(params, ctx, templateBasedDocument);

        File workingDir = getWorkingDir();
        File generated = new File(workingDir, "JXLSresult-"
                + System.currentTimeMillis());
        generated.createNewFile();

        File input = new File(workingDir, "JXLSInput-"
                + System.currentTimeMillis());
        input.createNewFile();

        sourceTemplateBlob.transferTo(input);

        XLSTransformer transformer = new XLSTransformer();

        transformer.transformXLS(input.getAbsolutePath(), ctx,
                generated.getAbsolutePath());

        input.delete();

        Blob newBlob = new FileBlob(generated);

        String templateFileName = sourceTemplateBlob.getFilename();

        // set the output file name
        String targetFileExt = FileUtils.getFileExtension(templateFileName);
        String targetFileName = FileUtils.getFileNameNoExt(templateBasedDocument.getAdaptedDoc().getTitle());
        targetFileName = targetFileName + "." + targetFileExt;
        newBlob.setFilename(targetFileName);

        // mark the file for automatic deletion on GC
        Framework.trackFile(generated, newBlob);
        return newBlob;

    }

    @Override
    public List<TemplateInput> getInitialParametersDefinition(Blob blob)
            throws Exception {
        return new ArrayList<TemplateInput>();
    }

}
