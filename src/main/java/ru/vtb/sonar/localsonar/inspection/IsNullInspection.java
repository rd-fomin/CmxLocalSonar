package ru.vtb.sonar.localsonar.inspection;

import ru.vtb.sonar.localsonar.fix.IsNullFix;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiLiteralExpression;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class IsNullInspection extends AbstractBaseJavaLocalInspectionTool {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (!(element instanceof PsiBinaryExpression)) {
                    return;
                }
                var binary = (PsiBinaryExpression) element;
                if (binary.getROperand() instanceof PsiLiteralExpression) {
                    var nill = (PsiLiteralExpression) binary.getROperand();
                    registerProblem(binary, nill, holder);
                }
                if (binary.getLOperand() instanceof PsiLiteralExpression) {
                    var nill = (PsiLiteralExpression) binary.getLOperand();
                    registerProblem(binary, nill, holder);
                }

            }
        };
    }

    private void registerProblem(PsiBinaryExpression binary, PsiLiteralExpression nill, @NotNull ProblemsHolder holder) {
        if (!"null".equals(nill.getText())) {
            return;
        }
        var desc = "";
        var isNon = false;
        if (JavaTokenType.EQEQ.equals(binary.getOperationTokenType())) {
            desc = "You should use Object.isNull instead";
            isNon = true;
        } else if (JavaTokenType.NE.equals(binary.getOperationTokenType())) {
            desc = "You should use Object.nonNull instead";
        } else {
            return;
        }
        holder.registerProblem(
                binary,
                desc,
                ProblemHighlightType.WARNING,
                new IsNullFix(binary, isNon));
    }

}
