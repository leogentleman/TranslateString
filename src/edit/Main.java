package edit;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.impl.ModuleImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndexFacade;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TestInputDialog;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import org.apache.http.util.TextUtils;
import translate.Translate;

import java.io.*;

/**
 * Created by chandler on 2017/6/29.
 */
public class Main extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        // TODO: insert action logic here
        final Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
        final Document document = editor.getDocument();
        final SelectionModel selectionModel = editor.getSelectionModel();
        final Project project = anActionEvent.getData(PlatformDataKeys.PROJECT);

        final int start = selectionModel.getSelectionStart();
        final int end = selectionModel.getSelectionEnd();
        String selectedItem = document.getText(new TextRange(start, end));

        //获取模块所在的路径
        Module module = FileIndexFacade.getInstance(project).getModuleForFile(((EditorImpl) editor).getVirtualFile());
        if (module != null) {
            String modulePath = module.getModuleFilePath();
            String stringFilePath = null;
            int index = 0;
            if (modulePath != null) {
                for (int i = modulePath.length() - 1; i >= 0; i--) {
                    if (modulePath.charAt(i) == '/') {
                        index++;
                        if (index == 1) {
                            if (i - 1 >= 0) {
                                stringFilePath = modulePath.substring(0, i);
                            }
                            break;
                        }
                    }
                    if (index == 1) {
                        break;
                    }
                }
            }

            VirtualFile vf = LocalFileSystem.getInstance().findFileByPath(modulePath);
            if (vf != null) {
                String txt = FileDocumentManager.getInstance().getDocument(vf).getText();
                if (txt.contains("<string name=\"" + selectedItem + "\">")) {
                    Messages.showMessageDialog("已经存在同名的字符串了！", "Warning!", Messages.getWarningIcon());
                    return;
                }
                int pos = txt.indexOf("RES_FOLDERS_RELATIVE_PATH");  //获取资源文件夹路径
                String resDir = null;
                if (pos >= 0) {
                    for (int i = pos; i < txt.length(); i++) {
                        if (txt.substring(i, i + 12).equals("$MODULE_DIR$")) {
                            i += 12;
                            resDir = stringFilePath;
                            for (int j = i; j < txt.length(); j++) {
                                if (txt.charAt(j) == '\"') {
                                    resDir += txt.substring(i, j);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }

                String translateString = Messages.showInputDialog("输入想要国际化的字符串(中文)", "Translate String", Messages.getInformationIcon());

                if (!TextUtils.isEmpty(resDir)) {
                    appendResource(project,selectedItem, resDir + "/values/strings.xml", translateString, "en");
                    appendResource(project,selectedItem, resDir + "/values-zh/strings.xml", translateString, "zh");
                    appendResource(project,selectedItem, resDir + "/values-zh-rHK/strings.xml", translateString, "cht");
                    appendResource(project,selectedItem, resDir + "/values-zh-rTW/strings.xml", translateString, "cht");
                }

            }
            }
        selectionModel.removeSelection();
    }

    private static class TranslateResult {
        String src;
        String dst;
    }
    private static class TranslateData{
        String from;
        String to;
        TranslateResult[] trans_result;
    }

    private void appendResource(Project project, String selectedItem, String stringFilePath, String query, String toLan) {
        VirtualFile vf = LocalFileSystem.getInstance().findFileByPath(stringFilePath);
        if (vf != null) {
            Document stringDoc = FileDocumentManager.getInstance().getDocument(vf);
            if (!TextUtils.isEmpty(query)) {
                String translated = null;
                if (toLan.equals("zh")) {
                    translated = query;
                } else {
                    try {
                        TranslateData data = new Gson().fromJson(Translate.translate(query, toLan), TranslateData.class);
                        translated = data.trans_result[0].dst;
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }
                if (!TextUtils.isEmpty(translated)) {
                    String appenStr = "\t<string name=\"" + selectedItem + "\">" + translated + "</string>\n";
                    new AppendResource(project, stringDoc, appenStr).execute();
                }
            }
        }

    }
}
