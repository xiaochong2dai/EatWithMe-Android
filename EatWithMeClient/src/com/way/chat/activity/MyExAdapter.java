package com.way.chat.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.way.chat.common.bean.User;
import com.way.util.GroupFriend;
import com.way.util.MyDate;

/**
 * 
 * 
 * @author way
 * 
 */
public class MyExAdapter extends BaseExpandableListAdapter {
	private int[] imgs = { R.drawable.icon, R.drawable.f1, R.drawable.f2,
			R.drawable.f3, R.drawable.f4, R.drawable.f5, R.drawable.f6,
			R.drawable.f7, R.drawable.f8, R.drawable.f9 };
	private Context context;
	private List<GroupFriend> group;

	public MyExAdapter(Context context, List<GroupFriend> group) {
		super();
		this.context = context;
		this.group = group;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.member_listview, null);
		}

		TextView title = (TextView) convertView.findViewById(R.id.content_001);
		title.setText(getGroup(groupPosition).toString());

		ImageView image = (ImageView) convertView.findViewById(R.id.tubiao);
		if (isExpanded)
			image.setBackgroundResource(R.drawable.group_unfold_arrow);
		else
			image.setBackgroundResource(R.drawable.group_fold_arrow);

		return convertView;
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public Object getGroup(int groupPosition) {
		return group.get(groupPosition).getGroupName();
	}

	public int getGroupCount() {
		return group.size();
	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.item, null);
		}
		final TextView title = (TextView) convertView
				.findViewById(R.id.name_item);
		final TextView title2 = (TextView) convertView
				.findViewById(R.id.id_item);
		ImageView icon = (ImageView) convertView
				.findViewById(R.id.imageView_item);
		final String name = group.get(groupPosition).getChild(childPosition)
				.getName();
		
		int status = group.get(groupPosition).getChild(childPosition).getIsOnline();
		
		final String id = group.get(groupPosition).getChild(childPosition)
				.getId()
				+ "";
		final int img = group.get(groupPosition).getChild(childPosition)
				.getImg();
		
		if (status==1)
		{
			if (name.length() == 3)
				title.setText(name + "		          		             Eat with me!");
			if (name.length() == 4)
				title.setText(name + "  	          		           Eat with me!");
			if (name.length() == 5)
				title.setText(name + "  	          		         Eat with me!");
			else
				title.setText(name + "  	          		        Eat with me!");
		}
		else
		{
			if (name.length() == 3)
				title.setText(name + "	     	          		       Not hungry");
			else if (name.length() == 4)
				title.setText(name + "	     	          	  	      Not hungry");
			else if (name.length() == 5)
				title.setText(name + "	     	          		    Not hungry");
			else
				title.setText(name + "	     	          		   Not hungry");
		}
		title2.setText("ID: "+id);
		icon.setImageResource(imgs[img]);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				User u = new User();
				u.setName(name);
				u.setId(Integer.parseInt(id));
				u.setImg(img);
				Intent intent = new Intent(context, ChatActivity.class);
				intent.putExtra("user", u);
				context.startActivity(intent);
				// Toast.makeText(Tab2.this, "Start chat", 0).show();

			}
		});
		return convertView;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public Object getChild(int groupPosition, int childPosition) {
		return group.get(groupPosition).getChild(childPosition);
	}

	public int getChildrenCount(int groupPosition) {
		return group.get(groupPosition).getChildSize();
	}

	/**
	 * Indicates whether the child and group IDs are stable across changes to
	 * the underlying data. 
	 * 
	 * @return whether or not the same ID always refers to the same object
	 * @see Adapter#hasStableIds()
	 */
	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	/**
	 * 
	 * 
	 * @param group
	 *           
	 */
	public void updata(List<GroupFriend> group) {
		this.group = null;
		this.group = group;
	}

}
