/*=========================================================================
 *
 *  PROJECT:  SlimRoms
 *            Team Slimroms (http://www.slimroms.net)
 *
 *  COPYRIGHT Copyright (C) 2013 Slimroms http://www.slimroms.net
 *            All rights reserved
 *
 *  LICENSE   http://www.gnu.org/licenses/gpl-2.0.html GNU/GPL
 *
 *  AUTHORS:     fronti90, blk_jack
 *  DESCRIPTION: MahdiCenter: manage your ROM
 *
 *=========================================================================
 */
package com.mahdi.center;

import com.mahdi.ota.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AboutMahdi extends Fragment{

    private LinearLayout mWebsite;
    private LinearLayout mGoogleplus;
    private LinearLayout mXda;
    private LinearLayout mSource;
    private LinearLayout mDonate;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mahdi_about, container, false);
        return view;
    }

    private final View.OnClickListener mActionLayouts = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mWebsite) {
                launchUrl("http://mirror.mahdi-rom.com/");
            } else if (v == mGoogleplus) {
                launchUrl("https://plus.google.com/u/0/communities/116540622179206449806");
            } else if (v == mXda) {
                launchUrl(getString(R.string.xda_url));
            } else if (v == mSource) {
                launchUrl("http://github.com/Mahdi-rom");
            } else if (v == mDonate) {
                launchUrl("http://forum.xda-developers.com/donatetome.php?u=4593553");
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //set LinearLayouts and onClickListeners
        mWebsite = (LinearLayout) getView().findViewById(R.id.website);
        mWebsite.setOnClickListener(mActionLayouts);

        mGoogleplus = (LinearLayout) getView().findViewById(R.id.googleplus);
        mGoogleplus.setOnClickListener(mActionLayouts);

        mXda = (LinearLayout) getView().findViewById(R.id.xda);
        mXda.setOnClickListener(mActionLayouts);

        mDonate = (LinearLayout) getView().findViewById(R.id.donate);
        mDonate.setOnClickListener(mActionLayouts);

        mSource = (LinearLayout) getView().findViewById(R.id.source);
        mSource.setOnClickListener(mActionLayouts);
    }

    private void launchUrl(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, uriUrl);
        urlIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(urlIntent);
    }
}
