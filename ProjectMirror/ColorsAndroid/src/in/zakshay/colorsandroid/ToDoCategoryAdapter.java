package in.zakshay.colorsandroid;

import in.zakshay.colorsandroid.Models.ToDoCategory;
import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ToDoCategoryAdapter extends ArrayAdapter<ToDoCategory> {

	private final Context mContext;

	public ToDoCategoryAdapter(Context context, int resource) {
		super(context, resource);
		this.mContext = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		final ToDoCategory currentCategory = getItem(position);
		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(R.layout.row_to_do, parent, false);
		}
		TextView categotyTextView = (TextView) row.findViewById(R.id.toDoText);
		categotyTextView.setText(currentCategory.getCategoryName());

		View circeView = row.findViewById(R.id.colorCircle);
		Drawable circle = circeView.getBackground();
		GradientDrawable circleShapeDrawable = (GradientDrawable) circle;
		circleShapeDrawable.setColor(getColor(currentCategory.getCategoryColor()));
		return row;
	}

	private int getColor(String colorName) {

		if (colorName.equals("Emrald"))
			return Color.argb(255, 0, 138, 0);
		else if (colorName.equals("Cobalt"))
			return Color.argb(255, 0, 80, 239);
		else if (colorName.equals("Violet"))
			return Color.argb(255, 170, 0, 255);
		else if (colorName.equals("Magenta"))
			return Color.argb(255, 216, 0, 115);
		else if (colorName.equals("Red"))
			return Color.argb(255, 229, 20, 0);
		else if (colorName.equals("Pink"))
			return Color.argb(255, 244, 114, 208);
		else if (colorName.equals("Orange"))
			return Color.argb(255, 250, 104, 0);
		else if (colorName.equals("Lime"))
			return Color.argb(255, 164, 196, 0);
		else if (colorName.equals("Cyan"))
			return Color.argb(255, 27, 161, 226);
		else if (colorName.equals("Brown"))
			return Color.argb(255, 130, 90, 44);
		else if (colorName.equals("Black"))
			return Color.argb(255, 0, 0, 0);
		else if (colorName.equals("Yellow"))
			return Color.argb(255, 227, 200, 0);
		else
			return Color.argb(255, 0, 0, 0);

	}

}
