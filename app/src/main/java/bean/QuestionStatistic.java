package bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ferry on 14/2/2018.
 */

public class QuestionStatistic implements Parcelable {
	public String question;
	public int percentage;

	public QuestionStatistic(String question, int percentage) {
		this.question = question;
		this.percentage = percentage;
	}

	public QuestionStatistic(Parcel in) {
		question = in.readString();
		percentage = in.readInt();
	}

	public static final Creator<QuestionStatistic> CREATOR = new Creator<QuestionStatistic>() {
		public QuestionStatistic createFromParcel(Parcel in) {
			return new QuestionStatistic(in);
		}

		public QuestionStatistic[] newArray(int size) {
			return new QuestionStatistic[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int i) {
		out.writeString(question);
		out.writeInt(percentage);
	}
}
