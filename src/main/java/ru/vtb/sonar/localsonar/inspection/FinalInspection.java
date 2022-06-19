package ru.vtb.sonar.localsonar.inspection;

import ru.vtb.sonar.localsonar.fix.RemoveFinalFix;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class FinalInspection extends AbstractBaseJavaLocalInspectionTool {

    @Override
    public @Nullable ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
        var params = method.getParameterList().getParameters();
        var file = method.getContainingFile();
        var holder = new ProblemsHolder(manager, file, isOnTheFly);
        Arrays.stream(params).forEach(jvmParameter ->
                Optional.ofNullable(jvmParameter.getModifierList())
                        .map(PsiElement::getChildren)
                        .map(Arrays::stream).stream()
                        .flatMap(stream -> stream)
                        .filter(elem -> "final".equals(elem.getText()))
                        .forEach(elem -> holder.registerProblem(
                                elem,
                                "Parameter should not be final",
                                ProblemHighlightType.WARNING,
                                new RemoveFinalFix(jvmParameter))));
        return holder.getResultsArray();
    }

}
