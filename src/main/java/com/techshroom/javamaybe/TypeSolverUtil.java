/*
 * This file is part of JavaMaybe, licensed under the MIT License (MIT).
 *
 * Copyright (c) TechShroom Studios <https://techshroom.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.techshroom.javamaybe;

import java.util.WeakHashMap;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.typesystem.Type;

public class TypeSolverUtil {

    private static final Node OBJECT_REFERENCE;
    static {
        // Extracts an in-context ObjectCreationExpr from the code
        CompilationUnit unit = JavaParser.parse("class T{{new Object().clone();}}");
        OBJECT_REFERENCE = unit.accept(new GenericVisitorAdapter<Node, Void>() {

            @Override
            public Node visit(ObjectCreationExpr n, Void arg) {
                return n;
            }
        }, null);
    }
    private static final WeakHashMap<JavaParserFacade, Type> SOLVED_OBJECT = new WeakHashMap<>();

    public static Type getType(Node node, JavaParserFacade typeSolver) {
        return runtimeType(typeSolver.getType(node), typeSolver);
    }

    public static Type runtimeType(Type type, JavaParserFacade typeSolver) {
        if (type.isTypeVariable()) {
            return SOLVED_OBJECT.computeIfAbsent(typeSolver, t -> t.getType(OBJECT_REFERENCE));
        }
        return type;
    }

}
