package edit;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;

/**
 * Created by chandler on 2017/6/30.
 */
public class AppendResource extends WriteCommandAction.Simple {
    Project project;
    Document document;
    String appendStr;

    public AppendResource(Project project, Document document, String appendStr) {
        super(project);
        this.project = project;
        this.document = document;
        this.appendStr  = appendStr;
    }
    @Override
    protected void run() throws Throwable {
        String text = document.getText();
        for (int i = text.length() - 12; i >= 0; i--) {
            if (text.substring(i, i + 12).equals("</resources>")) {
                document.insertString(i, appendStr);
                break;
            }
        }
    }
}
