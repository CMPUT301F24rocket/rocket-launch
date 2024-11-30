package com.example.rocket_launch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class UserHomepageFragment extends Fragment {

    private TextView welcome;
    private ImageView profilePicture;
    private ImageView defaultProfilePicture;
    private String username;
    private String profilePictureUrl; // URL or path to the profile picture

    // Constructor
    public UserHomepageFragment(String username, String profilePictureUrl) {
        this.username = username;
        this.profilePictureUrl = profilePictureUrl;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.user_homepage, container, false);

        // Initialize the TextView and set the welcome message
        welcome = view.findViewById(R.id.welcome_user);
        welcome.setText(String.format("Welcome %s", username));

        // Initialize ImageViews
        profilePicture = view.findViewById(R.id.profile_picture_homepage);
        defaultProfilePicture = view.findViewById(R.id.default_profile_picture_homepage); // Add this in your layout

        // Determine which profile picture to display
        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
            // Load profile picture using Glide
            profilePicture.setVisibility(View.VISIBLE);
            defaultProfilePicture.setVisibility(View.GONE);
            Glide.with(this)
                    .load(profilePictureUrl) // URL or file path
                    .placeholder(R.drawable.default_image) // Placeholder image
                    .error(R.drawable.default_image) // Error image
                    .circleCrop() // Optional: make the image circular
                    .into(profilePicture);
        } else {
            // No profile picture URL; use default profile picture
            profilePicture.setVisibility(View.GONE);
            defaultProfilePicture.setVisibility(View.VISIBLE);
            setDefaultProfilePicture(username);
        }

        return view;
    }

    // This function was taken by EditProfileFragment to generate the default user profile picture
    private void setDefaultProfilePicture(String userName) {
        // Default background color and text settings
        int width = 200;  // Width of the Bitmap
        int height = 200; // Height of the Bitmap
        int textColor = Color.BLACK;
        float textSize = 80f;

        // Load the background image (new_image) from resources
        Bitmap backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.new_image);
        Bitmap scaledBackground = Bitmap.createScaledBitmap(backgroundBitmap, width, height, false);

        // Create a new Bitmap and Canvas to draw
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw the background image
        canvas.drawBitmap(scaledBackground, 0, 0, null);

        // Set up Paint for the text
        Paint paint = new Paint();
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);

        // Use a premium built-in font (example: sans-serif-medium)
        paint.setTypeface(Typeface.create("serif", Typeface.NORMAL));

        // Draw the first letter of the user's name
        if (userName != null && !userName.isEmpty()) {
            String firstLetter = userName.substring(0, 1).toUpperCase();
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float x = width / 2f;
            float y = (height / 2f) - ((fontMetrics.ascent + fontMetrics.descent) / 2f);
            canvas.drawText(firstLetter, x, y, paint);
        }

        // Set the final Bitmap to the ImageView
        defaultProfilePicture.setImageBitmap(bitmap);
    }
}
