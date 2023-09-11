/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.github.antikyth.searchable.datagen.annotation.processor;

import io.github.antikyth.searchable.datagen.annotation.TranslationTarget;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("io.github.antikyth.searchable.datagen.annotation.Translation")
public class TranslationProcessor extends AbstractProcessor {
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (TypeElement annotation : annotations) {
			Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

			Map<Boolean, List<Element>> splitElements = annotatedElements.stream().collect(Collectors.partitioningBy(
				element -> element instanceof VariableElement variableElement
					&& variableElement.getConstantValue() instanceof String
					&& variableElement.getEnclosingElement() instanceof TypeElement
			));

			List<Element> validElements = splitElements.get(true);
			List<Element> invalidElements = splitElements.get(false);

			invalidElements.forEach(element -> this.processingEnv.getMessager().printMessage(
				Diagnostic.Kind.ERROR,
				"@Translation must be applied to String constants only"
			));

			if (validElements.isEmpty()) continue;

			roundEnv.getElementsAnnotatedWith(TranslationTarget.class).forEach(element -> {
			});

			validElements.forEach(element -> {
				VariableElement variableElement = (VariableElement) element;
				TypeElement enclosingElement = (TypeElement) variableElement.getEnclosingElement();

				enclosingElement.getEnclosedElements().forEach(enclosedElement -> {
					if (enclosedElement.getKind().equals(ElementKind.METHOD)) {
						// TODO
					}
				});
			});
		}

		return false;
	}
}
