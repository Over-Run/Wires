/*
 * MIT License
 *
 * Copyright (c) 2021 Overrun Organization
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package org.overrun.wires.util;

import net.minecraft.util.registry.Registry;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * @author squid233
 * @since 0.1.0
 */
public class AutoRegistry {
    @SuppressWarnings("unchecked")
    public static <T> void register(Class<?> clazz, Registry<T> registry) {
        String id = null;
        ModID modID = clazz.getAnnotation(ModID.class);
        if (modID != null) {
            id = modID.value();
        }
        for (Field field : clazz.getFields()) {
            if (field.isAnnotationPresent(Ignore.class)) {
                continue;
            }
            String name;
            CustomName cn = field.getAnnotation(CustomName.class);
            if (cn != null) {
                name = cn.value();
            } else {
                name = field.getName().toLowerCase();
            }
            String finalId;
            ModID fieldModID = field.getAnnotation(ModID.class);
            if (fieldModID != null) {
                finalId = fieldModID.value();
            } else {
                finalId = id;
            }
            try {
                Registry.register(registry, finalId + ":" + name, (T) field.get(null));
            } catch (IllegalAccessException e) {
                throw new RuntimeException();
            }
        }
    }

    @Target({TYPE, FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ModID {
        String value();
    }

    @Target(FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Ignore {}

    @Target(FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface CustomName {
        String value();
    }
}
