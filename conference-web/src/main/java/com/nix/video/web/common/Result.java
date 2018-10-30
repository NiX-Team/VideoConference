package com.nix.video.web.common;


import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Kiss
 * @date 2018/10/27 11:53
 */
public abstract class Result<S> implements Serializable {
    private final boolean success;
    private final S content;
    private transient final Throwable exception;
    private final String errorMsg;
    private final String errorCode;
    private final String errorLevel;

    private Result(boolean success, S content, Throwable exception, String errorMsg, String errorCode,
                   String errorLevel) {
        this.success = success;
        this.content = content;
        this.exception = exception;
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
        this.errorLevel = errorLevel;
    }

    public static <S> Result<S> of(Supplier<S> supplier) {
        try {
            S s = supplier.get();
            return success(s);
        } catch (Exception e) {
            return failure(e);
        }
    }

    public static <S> Result<S> success() {
        return new Success<>(null);
    }
    public static <S> Result<S> success(S success) {
        return new Success<>(success);
    }
    public static <S> Result<S> success(Supplier<S> success) {
        return new Success<>(success.get());
    }

    public static <T> Result<T> failure(Throwable failure, String errorMsg) {
        return new Failure<>(failure, errorMsg);
    }

    public static <T> Result<T> failure(Throwable failure, String errorMsg, String errorCode) {
        return new Failure<>(failure, errorMsg, errorCode);
    }

    public static <T>  Result<T> failure(Throwable exception) {
        return new Failure<>(exception, exception.getMessage());
    }

    public static <T> Result<T> failure(String errorMsg) {
        return new Failure<>(null, errorMsg);
    }

    public S get() {
        return content;
    }


    public final S getSuccessOrElse(Function<Result, S> alternative) {
        return fold(
                t -> t,
                t -> alternative.apply(t)
        );
    }

    public final S orElse(Function<Result, S> alternative) {
        return getSuccessOrElse(alternative);
    }

    public final Failure getFailureOrElse(Failure alternative) {
        return fold(
                t -> alternative,
                t -> t
        );
    }

    public final Result<S> onSuccess(Consumer<S> onSuccess) {
        Result<S> me = this;
        return fold(
                s -> {
                    onSuccess.accept(s);
                    return me;
                },
                s -> this
        );
    }

    @SuppressWarnings("unchecked")
    public final Result<S> onFailure(Consumer<Result> onFailure) {
        Result<S> me = this;
        return fold(
                s -> me,
                s -> {
                    onFailure.accept(s);
                    return s;
                }
        );
    }

    public final Result<S> logFail(Object request){
        return this.onFailure(result -> {
            String msg = result.errorMsg;
            if (msg == null) {
                msg = result.getException()==null?null:result.getException().getMessage();
            }
            if (msg == null) {
                msg = "";
            }
            if (request != null) {
                msg = String.format("request = %s, msg = %s", request, msg);
            }
        });
    }

    public final Result<S> logFail() {
        return logFail(null);
    }

    /**
     * Maps success to a new required success type, keeping the same failure type.
     */
    @SuppressWarnings("unchecked")
    public final <S2> Result<S2> map(Function<S, S2> f) {
        return fold(
                s -> success(f.apply(s)),
                fail -> fail
        );
    }

    @SuppressWarnings("unchecked")
    public final <S2> Result<S2> flatMap(Function<S, Result<S2>> onSuccess) {
        return fold(s -> {
                    try {
                        return onSuccess.apply(s);
                    } catch (Throwable e) {
                        return Result.failure(e);
                    }
                },
                f -> f
        );
    }

    public final <S2> Result<S2> bimap(Function<S, Result<S2>> onSuccess, Function<Failure, Result<S2>> onFailure) {
        return fold(
                onSuccess,
                onFailure
        );
    }

    public abstract <T> T fold(Function<S, T> onSuccess, Function<Failure, T> onFailure);

    public static class Failure<T> extends Result<T> {
        private static final long serialVersionUID = 1L;

        private Failure(Throwable failure, String errorMsg, String errorCode, String errorLevel) {
            super(false, null, failure, errorMsg, errorCode, errorLevel);
        }

        private Failure(Throwable failure, String errorMsg, String errorCode) {
            super(false, null, failure, errorMsg, errorCode, null);
        }

        private Failure(Throwable failure, String errorMsg) {
            super(false, null, failure, errorMsg, null, null);
        }

        @Override
        public <T1> T1 fold(Function<T, T1> onSuccess, Function<Failure, T1> onFailure) {
            return onFailure.apply(this);
        }

        @Override
        public T getContent() {
            return null;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

    }

    public static class Success<S> extends Result<S> {

        private static final long serialVersionUID = 1L;

        private Success(S success) {
            super(true, success, null, null, null, null);
        }

        @Override
        public <T> T fold(Function<S, T> onSuccess, Function<Failure, T> onFailure) {
            return onSuccess.apply(get());
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public S getContent() {
        return content;
    }

    public Throwable getException() {
        return exception;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorLevel() {
        return errorLevel;
    }
}
