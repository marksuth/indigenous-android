package com.indieweb.indigenous.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;

import com.indieweb.indigenous.R;
import com.indieweb.indigenous.model.IndigenousUser;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class Accounts {

    private final Context context;

    public Accounts(Context context) {
        this.context = context;
    }

    public IndigenousUser getCurrentUser() {
        IndigenousUser u = new IndigenousUser();

        SharedPreferences preferences = context.getSharedPreferences("indigenous", MODE_PRIVATE);
        String accountName = preferences.getString("account", "");
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccounts();
        if (accounts.length > 0) {
            for (Account account : accounts) {
                if (account.name.equals(accountName)) {
                    u.setValid(true);
                    u.setMe(accountName);
                    u.setAccessToken(accountManager.getUserData(account, "access_token"));
                    u.setTokenEndpoint(accountManager.getUserData(account, "token_endpoint"));
                    u.setAuthorizationEndpoint(accountManager.getUserData(account, "authorization_endpoint"));
                    u.setMicrosubEndpoint(accountManager.getUserData(account, "microsub_endpoint"));
                    u.setMicropubEndpoint(accountManager.getUserData(account, "micropub_endpoint"));
                }
            }
        }

        return u;
    }

    /**
     * Returns all accounts.
     *
     * @return Account[]
     */
    public Account[] getAllAccounts() {
        AccountManager accountManager = AccountManager.get(context);
        return accountManager.getAccounts();
    }

    /**
     * Switch account dialog.
     */
    public void switchAccount(final Activity a) {
        final List<String> accounts = new ArrayList<>();

        final IndigenousUser currentUser = new Accounts(context).getCurrentUser();
        final Account[] AllAccounts = new Accounts(context).getAllAccounts();
        for (Account account: AllAccounts) {
            accounts.add(account.name);
        }

        final CharSequence[] accountItems = accounts.toArray(new CharSequence[accounts.size()]);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Switch account");

        builder.setPositiveButton(context.getString(R.string.add_new_account),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                Intent IndieAuth = new Intent(context, com.indieweb.indigenous.indieauth.IndieAuth.class);
                context.startActivity(IndieAuth);
            }
        });
        builder.setCancelable(true);
        builder.setItems(accountItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!accounts.get(which).equals(currentUser.getMe())) {
                    SharedPreferences.Editor editor = context.getSharedPreferences("indigenous", MODE_PRIVATE).edit();
                    editor.putString("account", accounts.get(which));
                    editor.apply();
                    Intent Main = new Intent(context, com.indieweb.indigenous.MainActivity.class);
                    context.startActivity(Main);
                    a.finish();
                }

            }
        });
        builder.show();
    }
}
