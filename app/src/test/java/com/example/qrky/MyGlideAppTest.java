package com.example.qrky;

import static org.hamcrest.CoreMatchers.any;
import static org.mockito.AdditionalMatchers.eq;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.InputStream;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class MyGlideAppTest {

    @Test
    public void testRegisterComponents() {
        // Create a mock context, glide and registry
        Context context = Mockito.mock(Context.class);
        Glide glide = Mockito.mock(Glide.class);
        Registry registry = Mockito.mock(Registry.class);

        // Call the registerComponents method
        MyGlideApp myGlideApp = new MyGlideApp();
        myGlideApp.registerComponents(context, glide, registry);

        // Verify that the FirebaseImageLoader.Factory was appended to the registry for StorageReference.class and InputStream.class
        Mockito.verify(registry).append(eq(StorageReference.class), eq(InputStream.class), org.mockito.ArgumentMatchers.any(FirebaseImageLoader.Factory.class));

    }

    }

