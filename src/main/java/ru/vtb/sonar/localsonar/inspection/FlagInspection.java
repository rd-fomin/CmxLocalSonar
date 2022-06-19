package ru.vtb.sonar.localsonar.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class FlagInspection extends AbstractBaseJavaLocalInspectionTool {

    @Override
    public @Nullable ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
        var params = method.getParameterList().getParameters();
        var file = method.getContainingFile();
        var holder = new ProblemsHolder(manager, file, isOnTheFly);
        Arrays.stream(params).forEach(jvmParameter ->
                Optional.of(jvmParameter)
                        .filter(elem -> "boolean".equalsIgnoreCase(elem.getType().getCanonicalText()))
                        .map(PsiParameter::getSourceElement)
                        .ifPresent(elem -> holder.registerProblem(
                                elem,
                                "Parameter should not be boolean",
                                ProblemHighlightType.WARNING)));
        return holder.getResultsArray();
    }
}
