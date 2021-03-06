package uk.co.victoriajanedavis.chatapp.data.model.websocket

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.UUID

data class RejectedFriendRequestWsModel (
    @SerializedName("type") @Expose val type: String = RealtimeMessageTypes.REJECTED_FRIEND_REQUEST.type,
    @SerializedName("rejector_uuid") @Expose var rejectorUuid: UUID,
    @SerializedName("rejector_email") @Expose var rejectorEmail: String,
    @SerializedName("rejector_username") @Expose var rejectorUsername: String
) : RealtimeModel
