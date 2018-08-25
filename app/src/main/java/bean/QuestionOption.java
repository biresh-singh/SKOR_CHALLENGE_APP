package bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ferry on 14/2/2018.
 */

public class QuestionOption implements Parcelable {
	public String option;

	public QuestionOption() {
		this.option = "";
	}

	public QuestionOption(String option) {
		this.option = option;
	}

	public QuestionOption(Parcel in) {
		option = in.readString();
	}

	public QuestionOption(JSONObject jsonObject) {
		try {
			if (jsonObject.has("option")) {
				option = jsonObject.getString("option");
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static JSONObject toJSONObject(QuestionOption option) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("option", option.option);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	public static final Creator<QuestionOption> CREATOR = new Creator<QuestionOption>() {
		public QuestionOption createFromParcel(Parcel in) {
			return new QuestionOption(in);
		}

		public QuestionOption[] newArray(int size) {
			return new QuestionOption[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int i) {
		out.writeString(option);
	}
}
