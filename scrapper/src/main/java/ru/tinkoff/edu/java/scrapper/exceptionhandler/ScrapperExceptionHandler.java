package ru.tinkoff.edu.java.scrapper.exceptionhandler;

import ch.qos.logback.core.helpers.ThrowableToStringArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.tinkoff.edu.java.scrapper.dto.response.ApiErrorResponse;
import ru.tinkoff.edu.java.scrapper.exception.ChatNotFoundException;
import ru.tinkoff.edu.java.scrapper.exception.DuplicateChatException;
import ru.tinkoff.edu.java.scrapper.exception.DuplicateLinkException;
import ru.tinkoff.edu.java.scrapper.exception.LinkNotFoundException;
import ru.tinkoff.edu.java.scrapper.exception.ScrapperErrorConstants;

@RestControllerAdvice
public class ScrapperExceptionHandler {

	@ExceptionHandler({MethodArgumentNotValidException.class, MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
	public ResponseEntity<ApiErrorResponse> incorrectRequestParametersException(Exception e) {

		ApiErrorResponse apiErrorResponse = generateApiErrorResponse(e, ScrapperErrorConstants.INCORRECT_REQUEST_PARAMETERS);
		return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(LinkNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> linkNotFoundExceptionHandler(LinkNotFoundException e) {

		ApiErrorResponse apiErrorResponse = generateApiErrorResponse(e, ScrapperErrorConstants.LINK_NOT_FOUND); // пока не выбрасывается, тк нет данных
		return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ChatNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> chatNotFoundExceptionHandler(ChatNotFoundException e) {

		ApiErrorResponse apiErrorResponse = generateApiErrorResponse(e, ScrapperErrorConstants.CHAT_NOT_EXISTS); // пока не выбрасывается, тк нет данных
		return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(DuplicateLinkException.class)
	public ResponseEntity<ApiErrorResponse> duplicateLinkExceptionHandler(DuplicateLinkException e) {

		ApiErrorResponse apiErrorResponse = generateApiErrorResponse(e, ScrapperErrorConstants.DUPLICATE_LINK); // пока не выбрасывается, тк нет данных
		return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(DuplicateChatException.class)
	public ResponseEntity<ApiErrorResponse> duplicateChatExceptionHandler(DuplicateChatException e) {

		ApiErrorResponse apiErrorResponse = generateApiErrorResponse(e, ScrapperErrorConstants.DUPLICATE_CHAT); // пока не выбрасывается, тк нет данных
		return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
	}

	private ApiErrorResponse generateApiErrorResponse(Exception e, ScrapperErrorConstants constants) {

		return new ApiErrorResponse( // ApiErrorResponse заглушка
				constants.getMessage(),
				constants.getStatus().toString(),
				e.getClass().getName(),
				e.getMessage(),
				ThrowableToStringArray.convert(e)
		);
	}
}