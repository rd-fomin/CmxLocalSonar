package ru.vtb.sonar.localsonar.fix;

import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class RemoveFinalFix extends LocalQuickFixOnPsiElement {

    public RemoveFinalFix(@NotNull PsiElement element) {
        super(element);
    }

    @Override
    public @IntentionName
    @NotNull String getText() {
        return getFamilyName();
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile psiFile, @NotNull PsiElement start, @NotNull PsiElement end) {
        WriteCommandAction.runWriteCommandAction(project, "Remove Final Modifier", null, () -> {
            var param = (PsiParameter) start;
            Optional.ofNullable(param.getModifierList())
                    .map(PsiModifierList::getChildren)
                    .map(Arrays::stream).stream()
                    .flatMap(stream -> stream)
                    .filter(psiElement -> "final".equals(psiElement.getText()))
                    .forEach(PsiElement::delete);
        });
    }

    @Override
    public @IntentionFamilyName
    @NotNull String getFamilyName() {
        return "Remove final modifier";
    }
}
