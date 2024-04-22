package com.gdiwing.baselib.net;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.gdiwing.baselib.utils.CheckUtils;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

/**
 * Created by LtLei on 2019/6/2.
 * Email: fighting_our_life@foxmail.com
 * <p>
 * Description: ParameterizedTypeImpl 泛型的实体类解析
 */
public class ParameterizedTypeImpl implements ParameterizedType {
    private final Type ownerType;
    private final Type rawType;
    private final Type[] typeArguments;

    ParameterizedTypeImpl(@Nullable Type ownerType, @NonNull Type rawType, @NonNull Type... typeArguments) {
        // Require an owner type if the raw type needs it.
        if (rawType instanceof Class<?>
                && (ownerType == null) != (((Class<?>) rawType).getEnclosingClass() == null)) {
            throw new IllegalArgumentException();
        }

        for (Type typeArgument : typeArguments) {
            CheckUtils.checkNotNull(typeArgument, "typeArgument == null");
            CheckUtils.checkNotPrimitive(typeArgument);
        }

        this.ownerType = ownerType;
        this.rawType = rawType;
        this.typeArguments = typeArguments.clone();
    }

    @NonNull
    @Override
    public Type[] getActualTypeArguments() {
        return typeArguments.clone();
    }

    @Override
    public Type getOwnerType() {
        return ownerType;
    }

    @NonNull
    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(typeArguments)
                ^ rawType.hashCode()
                ^ (ownerType != null ? ownerType.hashCode() : 0);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof ParameterizedType && equalsInternal(this, (ParameterizedType) obj);
    }

    private boolean equalsInternal(Type a, Type b) {
        if (a == b) {
            return true;
        } else if (a instanceof Class) {
            return a.equals(b);
        } else if (a instanceof ParameterizedType) {
            if (!(b instanceof ParameterizedType)) {
                return false;
            }
            ParameterizedType pa = (ParameterizedType) a;
            ParameterizedType pb = (ParameterizedType) b;
            Object ownerA = pa.getOwnerType();
            Object ownerB = pb.getOwnerType();
            return (ownerA == ownerB || (ownerA != null && ownerA.equals(ownerB)))
                    && pa.getRawType().equals(pb.getRawType())
                    && Arrays.equals(pa.getActualTypeArguments(), pb.getActualTypeArguments());
        } else if (a instanceof GenericArrayType) {
            if (!(b instanceof GenericArrayType)) return false;
            GenericArrayType ga = (GenericArrayType) a;
            GenericArrayType gb = (GenericArrayType) b;
            return equalsInternal(ga.getGenericComponentType(), gb.getGenericComponentType());

        } else if (a instanceof WildcardType) {
            if (!(b instanceof WildcardType)) return false;
            WildcardType wa = (WildcardType) a;
            WildcardType wb = (WildcardType) b;
            return Arrays.equals(wa.getUpperBounds(), wb.getUpperBounds())
                    && Arrays.equals(wa.getLowerBounds(), wb.getLowerBounds());

        } else if (a instanceof TypeVariable) {
            if (!(b instanceof TypeVariable)) return false;
            TypeVariable<?> va = (TypeVariable<?>) a;
            TypeVariable<?> vb = (TypeVariable<?>) b;
            return va.getGenericDeclaration() == vb.getGenericDeclaration()
                    && va.getName().equals(vb.getName());

        } else {
            // This isn't a type we support!
            return false;
        }
    }
}
