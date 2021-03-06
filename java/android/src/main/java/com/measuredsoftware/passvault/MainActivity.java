// Copyright 2014 Neil Wilkinson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.measuredsoftware.passvault;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;

import com.google.gson.Gson;
import com.measuredsoftware.passvault.model.NavigationClickHandler;
import com.measuredsoftware.passvault.model.PasswordListAdapter;
import com.measuredsoftware.passvault.model.PasswordModel;
import com.measuredsoftware.passvault.model.UserPreferences;
import com.measuredsoftware.passvault.view.MenuScreen;
import com.measuredsoftware.passvault.view.PasswordList;
import com.measuredsoftware.passvault.view.PasswordListItem;
import com.measuredsoftware.passvault.view.PasswordPopup;

public class MainActivity extends AppCompatActivity implements MenuScreen.ChangedListener, PasswordListItem.EditItemListener, AbsListView.OnScrollListener
{
    public static final String RESULT_EXTRA = "krkesjfkejfe";

    public static final int REQUEST_CODE_ADD = 0;
    public static final int REQUEST_CODE_EDIT = 1;

    private PasswordList passwordList;
    private View addButton;
    private PasswordListAdapter dataModel;
    private MenuItem editButton;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private boolean scrolling;
    private NavigationClickHandler navigationClickHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        passwordList = (PasswordList) findViewById(R.id.password_list);
        passwordList.setOnScrollListener(this);
        final PasswordListAdapter adapter = new PasswordListAdapter(this, passwordList);
        passwordList.setAdapter(adapter);
        passwordList.getAdapter().registerDataSetObserver(new DataSetObserver()
        {
            @Override
            public void onChanged()
            {
                if (dataModel.isEmpty())
                {
                    if (dataModel.getMode() == PasswordListAdapter.Mode.EDIT)
                    {
                        toggleEditMode();
                    }
                }

                dataModel.storePasswords();

                updateEditButtonState();
            }
        });
        adapter.setEditItemListener(this);

        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.sliding_drawer);

        dataModel = passwordList.getPasswordListAdapter();

        addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                goToPasswordCreator();
            }
        });

        passwordList.setPasswordPopup((PasswordPopup) findViewById(R.id.password_popup));

        final MenuScreen menuScreen = (MenuScreen) findViewById(R.id.menu_screen);
        menuScreen.setListener(this);
        for (UserPreferences.Options option : UserPreferences.Options.values())
        {
            menuScreen.setOption(option.getViewId(), getUserPrefs().isOptionChecked(option));
        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);

        // Set the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationClickHandler = new NavigationClickHandler(dataModel, drawerLayout);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if (navigationClickHandler.toggleEditModeOnClick(scrolling))
                {
                    toggleEditMode();
                    if (!scrolling)
                    {
                        actionBarDrawerToggle.syncState();
                    }
                }

            }
        });

        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        editButton = toolbar.getMenu().findItem(R.id.edit_button);
        updateEditButtonState();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        boolean handled = true;

        final int id = item.getItemId();
        switch (id)
        {
            case R.id.edit_button:
                if (!scrolling)
                {
                    toggleEditMode();
                }
                break;
            default:
                handled = super.onOptionsItemSelected(item);
                break;
        }

        return handled;
    }

    private UserPreferences getUserPrefs()
    {
        return ((PassVaultApplication) getApplication()).getUserPrefs();
    }

    private void toggleEditMode()
    {
        final PasswordListAdapter.Mode newMode = dataModel.getMode() == PasswordListAdapter.Mode.EDIT
                ? PasswordListAdapter.Mode.NORMAL
                : PasswordListAdapter.Mode.EDIT;
        dataModel.setMode(newMode);
        passwordList.setMode(newMode);

        editButton.setVisible(newMode == PasswordListAdapter.Mode.NORMAL);
        addButton.setVisibility(newMode == PasswordListAdapter.Mode.NORMAL ? View.VISIBLE : View.INVISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(newMode == PasswordListAdapter.Mode.EDIT);
    }

    private void updateEditButtonState()
    {
        editButton.setVisible(passwordList.getCount() > 0);
    }

    private void goToPasswordCreator()
    {
        final int addButtonSize = Math.round(addButton.getWidth() / 2);
        final int fabX = (int) (addButton.getX() + addButtonSize);
        final int fabY = (int) (addButton.getY() + addButtonSize);
        startActivityForResult(NewPasswordActivity.createLaunchIntent(this, fabX, fabY), REQUEST_CODE_ADD);
    }

    private void goToPasswordEditor(final PasswordModel model)
    {
        final Gson gson = new Gson();
        startActivityForResult(EditPasswordActivity.createLaunchIntent(this, gson.toJson(model)), REQUEST_CODE_EDIT);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            final Gson gson = new Gson();
            final PasswordModel model = gson.fromJson(data.getStringExtra(RESULT_EXTRA), PasswordModel.class);

            if (requestCode == REQUEST_CODE_ADD)
            {
                dataModel.addPassword(model);
            }
            else if (requestCode == REQUEST_CODE_EDIT)
            {
                dataModel.editPassword(model);
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (dataModel.getMode() == PasswordListAdapter.Mode.EDIT)
        {
            disableEditModeAndSyncDrawToggle();
        }
        passwordList.closePopup();
    }

    @Override
    public void onBackPressed()
    {
        if (dataModel.getMode() == PasswordListAdapter.Mode.EDIT)
        {
            disableEditModeAndSyncDrawToggle();
        }
        else
        {
            super.onBackPressed();
        }
    }

    private void disableEditModeAndSyncDrawToggle()
    {
        toggleEditMode();
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void optionChanged(final int viewId, final boolean checked)
    {
        final UserPreferences.Options option = UserPreferences.Options.getOptionFromViewId(viewId);

        if (option != null)
        {
            getUserPrefs().setOptionChecked(option, checked);
        }
    }

    @Override
    public void onItemChosenForEdit(final PasswordModel item)
    {
        goToPasswordEditor(item);
    }

    @Override
    public void onOpenPopup(final PasswordModel item, final int x, final int y)
    {
        passwordList.openPopup(x, y, item.getPassword(), getUserPrefs().isOptionChecked(UserPreferences.Options.OBSCURE_PASSWORDS));
    }

    @Override
    public void onScrollStateChanged(final AbsListView view, final int scrollState)
    {
        scrolling = scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    }

    @Override
    public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount)
    {
        // not used
    }
}
