package org.dcxz.designdigger.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.dcxz.designdigger.R;
import org.dcxz.designdigger.activity.LoginActivity;
import org.dcxz.designdigger.app.App;
import org.dcxz.designdigger.dao.DaoManager;
import org.dcxz.designdigger.framework.BaseActivity;
import org.dcxz.designdigger.framework.BaseFragment;
import org.dcxz.designdigger.util.API;

/**
 * <br/>
 * Created by OvO on 2016/12/16.<br/>
 * ChangeLog :
 * <pre>
 * </pre>
 */

public class MenuFragment extends BaseFragment {
    public static final String TAG = "MenuFragment";
    private DaoManager manager;
    private ImageView avatar;
    private TextView signUp, signIn, signOut;
    private TextView settings;
    private AlertDialog dialog;
    private BroadcastReceiver receiver;

    @Override
    protected int setContentViewImp() {
        return R.layout.fragment_menu;
    }

    protected void initView(BaseActivity activity, View view) {
        avatar = (ImageView) view.findViewById(R.id.menu_avatar);
        signUp = (TextView) view.findViewById(R.id.menu_signUp);
        signIn = (TextView) view.findViewById(R.id.menu_signIn);
        signOut = (TextView) view.findViewById(R.id.menu_signOut);
        settings = (TextView) view.findViewById(R.id.menu_settings);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isUserLogined(true);
            }
        };
    }

    protected void initData(final BaseActivity activity, Bundle savedInstanceState) {
        manager = DaoManager.getInstance(activity);
        String accessToken = manager.getAccessToken();
        activity.registerReceiver(receiver, new IntentFilter(LoginActivity.TAG));
        isUserLogined(!accessToken.equals(API.Oauth2.ACCESS_TOKEN_DEFAULT));

        dialog = new AlertDialog.Builder(activity)
                .setTitle("Sign out")
                .setMessage("You are going to sign out")
                .setPositiveButton(
                        "Sign out",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                manager.setAccessToken(API.Oauth2.ACCESS_TOKEN_DEFAULT);//用默认access_token替换用户access_token
                                App.updateHeader();//更新内存和请求头部的access_token
                                isUserLogined(false);
                                activity.sendBroadcast(new Intent(TAG));//用户注销广播
                            }
                        })
                .setNegativeButton("Cancel", null).create();
    }

    /**
     * Sign in/Sign up 是否不可见,以及Sign out是否可见
     *
     * @param visibility true:Sign out 可见,其他不可见
     */
    private void isUserLogined(boolean visibility) {
        if (visibility) {
            signUp.setVisibility(View.INVISIBLE);
            signIn.setVisibility(View.INVISIBLE);
            signOut.setVisibility(View.VISIBLE);
            avatar.setImageBitmap(manager.getAvatar());
        } else {
            signUp.setVisibility(View.VISIBLE);
            signIn.setVisibility(View.VISIBLE);
            signOut.setVisibility(View.INVISIBLE);
            avatar.setImageResource(R.mipmap.dribbble_ball_mark);
        }
    }

    protected void initAdapter(BaseActivity activity) {
    }

    protected void initListener(final BaseActivity activity) {
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Avatar", Toast.LENGTH_SHORT).show();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Sign Up", Toast.LENGTH_SHORT).show();
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, LoginActivity.class).putExtra("STATE", MenuFragment.TAG));
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Settings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void handleMessageImp(Message msg) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(receiver);
    }

}