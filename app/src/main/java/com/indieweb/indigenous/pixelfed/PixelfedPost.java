package com.indieweb.indigenous.pixelfed;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.toolbox.HttpHeaderParser;
import com.indieweb.indigenous.R;
import com.indieweb.indigenous.model.User;
import com.indieweb.indigenous.post.PostBase;
import com.indieweb.indigenous.util.HTTPRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class PixelfedPost extends PostBase {

    public PixelfedPost(Context context, User user) {
        super(context, user);
    }

    @Override
    public String getMainPostTitle() {
        return this.getContext().getString(R.string.add_post);
    }

    @Override
    public String getEndpoint(boolean isMediaRequest) {
        if (isMediaRequest) {
            return this.getUser().getMe() + "/api/v1/media";
        }
        return this.getUser().getMe() + "/api/v1/statuses";
    }

    @Override
    public String getFileFromMediaResponse(NetworkResponse response) {
        String data;
        try {
            data = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
        }
        catch (UnsupportedEncodingException ignored) {
            data = new String(response.data);
        }
        String fileId = "";
        try {
            JSONObject o = new JSONObject(data);
            fileId = o.getString("id");
        }
        catch (JSONException ignored) {}

        return fileId;
    }

    @Override
    public boolean supports(String name) {
        boolean supported = true;

        switch (name) {
            case FEATURE_TITLE:
            case FEATURE_CATEGORIES:
            case FEATURE_CONTACTS:
                supported = false;
        }

        return supported;
    }

    @Override
    public boolean supportsPostParam(String name) {
        boolean supported = true;
        switch (name) {
            case POST_PARAM_H:
            case POST_PARAM_PUBLISHED:
            case POST_PARAM_POST_STATUS:
                supported = false;
                break;
        }

        return supported;
    }

    @Override
    public String getPostParamName(String name) {
        String paramName = name;
        switch (name) {
            case POST_PARAM_CONTENT:
                paramName = "status";
                break;
            case POST_PARAM_PUBLISHED:
                paramName = "scheduled_at";
                break;
            case POST_PARAM_REPLY:
                paramName = "in_reply_to_id";
                break;
            case POST_PARAM_PHOTO:
            case POST_PARAM_VIDEO:
                paramName = "media_ids";
                break;
        }

        return paramName;
    }

    @Override
    public void deletePost(String channelId, String id) {
        String endpoint = getUser().getMe() + "/api/v1/statuses/" + id;
        HTTPRequest r = new HTTPRequest(null, getUser(), getContext());
        r.doDeleteRequest(endpoint, null);
    }
}
