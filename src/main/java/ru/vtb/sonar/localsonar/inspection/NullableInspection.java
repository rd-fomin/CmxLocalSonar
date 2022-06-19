package ru.vtb.sonar.localsonar.inspection;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class NullableInspection extends AbstractBaseJavaLocalInspectionTool {
    private static final String NULLABLE = "@Nullable";
    private static final String NOT_NULL = "@NotNull";

    @Override
    public ProblemDescriptor @Nullable [] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
        var file = method.getContainingFile();
        var holder = new ProblemsHolder(manager, file, isOnTheFly);
        var hasNullable = Arrays.stream(method.getAnnotations()).map(PsiAnnotation::getText).anyMatch(NULLABLE::equals);
        var hasNotNull = Arrays.stream(method.getAnnotations()).map(PsiAnnotation::getText).anyMatch(NOT_NULL::equals);
        var isVoid = Optional.ofNullable(method.getReturnType()).map(PsiType::getCanonicalText).filter("void"::equals);
        if (isVoid.isPresent()) {
            return ProblemDescriptor.EMPTY_ARRAY;
        }
        if (hasNullable || hasNotNull) {
            return ProblemDescriptor.EMPTY_ARRAY;
        }
        holder.registerProblem(
                Objects.requireNonNull(method.getIdentifyingElement()),
                "You should use " + NULLABLE + " or " + NOT_NULL + " on method",
                ProblemHighlightType.WARNING);
        return holder.getResultsArray();
    }
}
