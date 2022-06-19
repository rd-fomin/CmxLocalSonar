package ru.vtb.sonar.localsonar.fix;

import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;


public class IsNullFix extends LocalQuickFixOnPsiElement {
    private final boolean isNon;

    public IsNullFix(@NotNull PsiElement element, boolean isNon) {
        super(element);
        this.isNon = isNon;
    }

    public IsNullFix(PsiElement startElement, PsiElement endElement, boolean isNon) {
        super(startElement, endElement);
        this.isNon = isNon;
    }

    @Override
    public @IntentionName
    @NotNull String getText() {
        return getFamilyName();
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile psiFile, @NotNull PsiElement start, @NotNull PsiElement end) {
        String desc = isNon ? "Objects.isNull(%s)" : "Objects.nonNull(%s)";
        var action = isNon ? "Change == Null to Objects.IsNull" : "Change != Null to Objects.NonNull";
        WriteCommandAction.runWriteCommandAction(project, action, null, () -> {
            var binary = (PsiBinaryExpression) start;
            var factory = JavaPsiFacade.getInstance(project).getElementFactory();
            binary.replace(factory.createExpressionFromText(String.format(desc, binary.getLOperand().getText()), null));
        });
    }

    @Override
    public @IntentionFamilyName
    @NotNull String getFamilyName() {
        if (isNon) {
            return "Change == null to Objects.isNull";
        } else {
            return "Change != null to Objects.nonNull";
        }
    }
}
