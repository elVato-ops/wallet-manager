package walletmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

import static walletmanager.exception.ErrorCode.*;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(UserValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserValidationException(UserValidationException e)
    {
        return response(e.getMessage(), USER_DATA_INVALID);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e)
    {
        return response(e.getMessage(), USER_NOT_FOUND);
    }

    @ExceptionHandler(AccountValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAccountValidationException(AccountValidationException e)
    {
        return response(e.getMessage(), ACCOUNT_DATA_INVALID);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleAccountNotFoundException(AccountNotFoundException e)
    {
        return response(e.getMessage(), ACCOUNT_NOT_FOUND);
    }

    @ExceptionHandler(TransactionValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTransactionValidationException(TransactionValidationException e)
    {
        return response(e.getMessage(), TRANSACTION_DATA_INVALID);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleInsufficientFoundsException(InsufficientFundsException e)
    {
        return response(e.getMessage(), INSUFFICIENT_FUNDS);
    }

    @ExceptionHandler(IllegalTransactionException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIllegalTransactionException(IllegalTransactionException e)
    {
        return response(e.getMessage(), ILLEGAL_TRANSACTION);
    }

    @ExceptionHandler(ConcurrentTransferException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConcurrentTransferException(ConcurrentTransferException e)
    {
        return response(e.getMessage(), CONCURRENT_TRANSACTION);
    }

    @ExceptionHandler(DifferentCurrencyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDifferentCurrencyException(DifferentCurrencyException e)
    {
        return response(e.getMessage(), CURRENCY_MISMATCH);
    }

    private ErrorResponse response(String message, ErrorCode errorCode)
    {
        return new ErrorResponse(message, errorCode, Instant.now());
    }
}
