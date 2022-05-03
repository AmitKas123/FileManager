package com.example.filemanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private boolean isFileManager=false;
private boolean selection[];
private  File[] files;
private int fileFoundCount;
private  List<String> filelist;
private Button refresh;
private File dir;
private  String currentpath;
private  String copypath;
private boolean onlongclick;
private  int selectIndex;
private void deleteFileOrFolder(File fileOrFolder){
    if(fileOrFolder.isDirectory())
    {
        if(fileOrFolder.list().length==0){
            fileOrFolder.delete();
        }

    else {
            String[] files = fileOrFolder.list();
            for (String temp : files) {

                File fiteToDatabase = new File(fileOrFolder, temp);
                deleteFileOrFolder(fiteToDatabase);
            }
            if (fileOrFolder.list().length == 0) {
                fileOrFolder.delete();
            } }}
    else {
                fileOrFolder.delete();
            }
        }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!isFileManager) {

            currentpath = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
            dir = new File(currentpath);
            files = dir.listFiles();
            final String rootpath=currentpath.substring(0,currentpath.lastIndexOf('/'));
            final TextView pathout = findViewById(R.id.textview2);


            pathout.setText(currentpath.substring(currentpath.lastIndexOf('/') + 1));
            fileFoundCount = files.length;
            final ListView listView = findViewById(R.id.recycle);
            baseAdapter baseAdapters;
            baseAdapters = new baseAdapter();
            listView.setAdapter(baseAdapters);
            filelist = new ArrayList<>();
            for (int j = 0; j < fileFoundCount; j++) {
                filelist.add(String.valueOf(files[j].getAbsolutePath()));
            }
            baseAdapters.setData(filelist);
            isFileManager = true;
            selection = new boolean[files.length];
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long id) {
                    onlongclick=true;
                    selection[i] = !selection[i];
                    baseAdapters.setSelection(selection);
                    int isselected=0;
                    for (boolean aSelection : selection) {
                        if (aSelection) {
                            isselected++;
                        }
                    }
                    if (isselected>0) {
                        if(isselected==1)
                        {
                            selectIndex=i;
                            findViewById(R.id.Rename).setVisibility(View.VISIBLE);
                        }
                        else {
                            findViewById(R.id.Rename).setVisibility(View.GONE);
                        }
                        findViewById(R.id.ll2).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.ll2).setVisibility(View.GONE);
                    }

new Handler().postDelayed(new Runnable() {
    @Override
    public void run() {
        onlongclick=false;

    }
},600);
                    return false;
                }
            });
              refresh = findViewById(R.id.refresh);
            final Button cfolder = findViewById(R.id.in);
            final Button b1 = findViewById(R.id.button1);
            final Button back = findViewById(R.id.back);
            final Button rename = findViewById(R.id.Rename);
            final Button copy = findViewById(R.id.copy);
            final Button paste = findViewById(R.id.paste);
            copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     copypath=files[selectIndex].getAbsolutePath();

                    selection=new boolean[files.length];

                        baseAdapters.setSelection(selection);
                      findViewById(R.id.paste).setVisibility(View.VISIBLE);
                }
            });
            paste.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    paste.setVisibility(View.GONE);
                    String destpath=currentpath+copypath.substring(copypath.lastIndexOf('/'));
                    copyFile(new String(copypath),new String(currentpath),new String(destpath));
                    files=new File(currentpath).listFiles();
                    selection=new boolean[files.length];
                    baseAdapters.setSelection(selection);
                    refresh.callOnClick();
                }
            });
            rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog.Builder renamedialog = new AlertDialog.Builder(MainActivity.this);
                    renamedialog.setTitle("RENAME");
                    final EditText editText1 = new EditText(MainActivity.this);
                    String renamepath = files[selectIndex].getAbsolutePath();

                    editText1.setText(renamepath.substring(renamepath.lastIndexOf('/')));
                    editText1.setInputType(InputType.TYPE_CLASS_TEXT);
                    renamedialog.setView(editText1);
                    renamedialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String s = new File(renamepath).getParent() + "/" + editText1.getText();

                            File newfile = new File(s);
                            new File(renamepath).renameTo(newfile);
                            refresh.callOnClick();
selection=new boolean[files.length];
baseAdapters.setSelection(selection);
                        }
                    });
                    renamedialog.show();


                }
            });

            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    files = dir.listFiles();
                    if(files==null) {
                        return;

                    }
                    fileFoundCount = files.length;
                    filelist.clear();
                    for (int j = 0; j < fileFoundCount; j++) {
                        filelist.add(String.valueOf(files[j].getAbsolutePath()));
                    }
                    baseAdapters.setData(filelist);

                }
            });
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(currentpath.equals(rootpath)){
                        return;
                    }
                    currentpath=currentpath.substring(0,currentpath.lastIndexOf('/'));
                     dir=new File(currentpath);
                    refresh.callOnClick();
                    pathout.setText(currentpath.substring(currentpath.lastIndexOf('/') + 1));
                   selection=new boolean[files.length];
                    baseAdapters.setSelection(selection);
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int i, long l) {
                    currentpath=files[i].getAbsolutePath();
new Handler().postDelayed(new Runnable() {
    @Override
    public void run() {
        if(!onlongclick) {
            if(files[i].isDirectory()){
            dir = new File(currentpath);
            pathout.setText(currentpath.substring(currentpath.lastIndexOf('/') + 1));
            refresh.callOnClick();
        }}
    }
},50);
                    selection=new boolean[files.length];
                    baseAdapters.setSelection(selection);

                }
            });

            cfolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final AlertDialog.Builder newfolder = new AlertDialog.Builder(MainActivity.this);
                    newfolder.setTitle(" New Folder");
                    final EditText input = new EditText(MainActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    newfolder.setView(input);
                    newfolder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final File newfolderC = new File(currentpath+ "/" + input.getText());
                            if (!newfolderC.exists()) {
                                newfolderC.mkdir();
                                refresh.callOnClick();
                            }

                        }
                    });
                    newfolder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                        }
                    });
                    newfolder.show();
                }
            });



            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog.Builder deteleD = new AlertDialog.Builder(MainActivity.this);
                    deteleD.setTitle("Delete");
                    deteleD.setMessage("Do you really want to delete");
                    deteleD.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (int j = 0; j < files.length; j++) {
                                if (selection[j]) {
                                    deleteFileOrFolder(files[j]);
                                    selection[j] = false;
                                }
                            }
                            refresh.callOnClick();
                        }
                    });
                    deteleD.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                        }
                    });
                    deteleD.show();
                }
            });
            isFileManager=true;

        }
else {
    refresh.callOnClick();
        }
        runtimePermission();
    }

    public void runtimePermission() {
        Dexter.withContext(MainActivity.this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }
   /* private void copy(File src,File drs)
    {
        try {
            InputStream in=new FileInputStream(src);
            OutputStream output=new FileOutputStream(drs);
            byte[] buf=new byte[1024];
            int len;
            while ((len=in.read(buf))>0)
            {
                output.write(buf,0,len);
            }
            in.close();
            output.close();
        }

    catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
   private void copyFile(String inputPath, String inputFile, String outputPath) {

       InputStream in = null;
       OutputStream out = null;
       try {

           //create output directory if it doesn't exist
           File dir = new File (outputPath);
           if (!dir.exists())
           {
               dir.mkdirs();
           }


           in = new FileInputStream(inputPath + inputFile);
           out = new FileOutputStream(outputPath + inputFile);

           byte[] buffer = new byte[1024];
           int read;
           while ((read = in.read(buffer)) != -1) {
               out.write(buffer, 0, read);
           }
           in.close();
           in = null;

           // write the output file (You have now copied the file)
           out.flush();
           out.close();
           out = null;

       }  catch (FileNotFoundException fnfe1) {
           Log.e("tag", fnfe1.getMessage());
       }
       catch (Exception e) {
           Log.e("tag", e.getMessage());
       }

   }

}
