
package it.unipd.dei.sagrone.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 * Defines all the possible error codes for this application.
 */
public enum ErrorCode {


    /**
     *  Output media type not specified
     */
    OUTPUT_MEDIA_TYPE_NOT_SPECIFIED("E4A1", HttpServletResponse.SC_BAD_REQUEST,"Output media type not specified."),

    /**
     *  Unsupported output media type
     */
    UNSUPPORTED_OUTPUT_MEDIA_TYPE("E4A2", HttpServletResponse.SC_NOT_ACCEPTABLE,"Unsupported output media type."),

    /**
     *  Input media type not specified
     */
    INPUT_MEDIA_TYPE_NOT_SPECIFIED("E4A3", HttpServletResponse.SC_BAD_REQUEST,"Input media type not specified."),


    /**
     *  Unsupported input media type
     */
    UNSUPPORTED_INPUT_MEDIA_TYPE("E4A4", HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,"Unsupported input media type."),

    /**
     *  Unsupported operation
     */
    UNSUPPORTED_OPERATION("E4A5", HttpServletResponse.SC_METHOD_NOT_ALLOWED,"Unsupported operation."),

    /**
     *  Unknown resource requested
     */
    UNKNOWN_RESOURCE("E4A6", HttpServletResponse.SC_NOT_FOUND,"Unknown resource requested."),

    /**
     *  Wrong URI format
     */
    WRONG_URI_FORMAT("E4A7", HttpServletResponse.SC_BAD_REQUEST,"Wrong URI format."),



    /**
     *  Wrong resource provided
     */
    WRONG_RESOURCE("E4A8", HttpServletResponse.SC_BAD_REQUEST,"Wrong resource provided."),

    /**
     *  Wrongly formatted resource provided
     */
    WRONGLY_FORMATTED_RESOURCE("E4A9", HttpServletResponse.SC_BAD_REQUEST,"Wrongly formatted resource provided."),



    /**
     * One or more input fields are empty or missing.
     */
    EMPTY_INPUT_FIELDS("E4B1", HttpServletResponse.SC_BAD_REQUEST, "One or more input fields are empty or missing."),

    /**
     * One or more input fields are wrong.
     */
    WRONG_INPUT_FIELDS("E4B2", HttpServletResponse.SC_BAD_REQUEST, "One or more input fields are wrong."),


    /**
     * Authorization needed
     */
    UNAUTHORIZED_ACCESS("E4B3", HttpServletResponse.SC_UNAUTHORIZED, "Authorization needed."),



    /**
     * Unexpected error while processing a resource
     */
    UNEXPECTED_ERROR("E5A1", HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Unexpected error while processing a resource."),


    /**
     * Resource already exists
     */
    EXISTING_RESOURCE("E5A2", HttpServletResponse.SC_CONFLICT,"Resource already exists."),

    /**
     * Resource not found
     */
    RESOURCE_NOT_FOUND("E5A3", HttpServletResponse.SC_NOT_FOUND,"Resource already exists."),

    /**
     * Cannot modify a resource because other resources depend on it
     */
    DEPENDENT_RESOURCE("E5A4", HttpServletResponse.SC_CONFLICT,"Cannot modify a resource because other resources depend on it."),

    /**
     * Cannot create resource
     */
    CANNOT_CREATE_RESOURCE("E5A4", HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Cannot create resource.");








    /**
     * the error code.
     */
    private final String errorCode;

    /**
     * the http error related to the error code.
     */
    private final int httpCode;

    /**
     * the error message to be shown.
     */
    private final String errorMessage;

    /**
     * Construct an instance of ErrorCode.
     * @param errorCode the error code.
     * @param httpCode the http error related to the error code.
     * @param errorMessage the error message to be shown.
     */
    ErrorCode(String errorCode, int httpCode, String errorMessage) {
        this.errorCode = errorCode;
        this.httpCode = httpCode;
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the HTTP error code.
     * @return Returns the HTTP error code.
     */
    public int getHTTPCode() {
        return httpCode;
    }

    /**
     * Returns the error message.
     * @return Returns the error message.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Returns the error code.
     * @return Returns the error code.
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Fetches data in a JSON object
     * @return JSONObject
     */
    public JSONObject toJSON() {
        JSONObject data = new JSONObject();
        data.put("code", errorCode);
        data.put("message", errorMessage);
        JSONObject info = new JSONObject();
        info.put("error", data);
        return info;
    }


}
