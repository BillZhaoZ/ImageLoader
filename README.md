# ImageLoader

图片加载框架（面向对象的原则）
    
    单一职责原则
    开闭原则
    里氏替换原则
    依赖倒置原则
    接口隔离原则
    
        
1、磁盘缓存
    
    /**
     * 磁盘缓存
     * <p>
     * 一、普通的磁盘缓存
     * 二、disklrucache
     * <p>
     * Created by Bill on 2017/12/7.
     */
    public class DiskCache implements ImageCache {
    
        private Context mContext;
        private String url;
        private String cacheDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Image/picsCache";
        private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50;// 50MB
        private DiskLruCache mDiskLruCache;
    
        public DiskCache() {
        }
    
        public DiskCache(Context mContext, String url) {
            this.mContext = mContext;
            this.url = url;
            init();
        }
    
        /**
         * 初始化disklrucache
         */
        private void init() {
    
            try {
                String fileName = MD5Encoder.encode(url);
                //  File diskCacheDir = new File(cacheDir, fileName);
                File diskCacheDir = loadUtil.getDiskCacheDir(mContext, fileName);
    
                if (!diskCacheDir.exists()) {
                    diskCacheDir.mkdirs();
                }
    
                mDiskLruCache = DiskLruCache.open(diskCacheDir, loadUtil.getAppVersion(mContext), 1, DISK_CACHE_SIZE);
    
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    
    
        @Override
        public Bitmap get(String url) {
            // 从本地文件获取图片
    
            // 第一种
            /* try {
                String fileName = MD5Encoder.encode(url);
                File file = new File(cacheDir, fileName);
    
                if (file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
    
                    Log.e("cache", "获取图片：来自于磁盘缓存： " + cacheDir);
                    return bitmap;
                }
    
            } catch (Exception e) {
                e.printStackTrace();
            }
    
            return null;*/
    
    
            // 第二种
            Bitmap bitmap = null;
            String key = null;
    
            try {
                key = MD5Encoder.encode(url);
                DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);
    
                if (snapShot != null) {
                    InputStream is = snapShot.getInputStream(0);
                    bitmap = BitmapFactory.decodeStream(is);
                }
    
            } catch (Exception e) {
                e.printStackTrace();
            }
    
            if (bitmap != null) {
                Log.e("cache", "获取图片：来自于磁盘缓存 === " + cacheDir + "===" + bitmap);
            } else {
                Log.e("cache", "获取图片为空 === 位置：" + cacheDir);
            }
    
            return bitmap;
        }
    
        @Override
        public void put(String url, Bitmap bitmap) {
    
            // 第二种
            try {
                String key = MD5Encoder.encode(url);
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
    
                if (editor != null) {
                    OutputStream outputStream = editor.newOutputStream(0);
    
                    // 写入图片到本地文件
                    if (loadUtil.downloadUrlToStream(url, outputStream)) {
                        editor.commit();
                    } else {
                        editor.abort();
                    }
                }
    
                Log.e("cache", "成功写入磁盘缓存 === " + cacheDir);
    
                mDiskLruCache.flush();
    
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("cache", "写入磁盘缓存失败 === " + e.toString());
            }
    
    
            // 第一种
           /* // 将bitmap写入文件中
            try {
                // 文件的名字
                String fileName = MD5Encoder.encode(url);
    
                // 创建文件流，指向该路径，文件名叫做fileName
                File file = new File(cacheDir, fileName);
    
                // file其实是图片，它的父级File是文件夹，判断一下文件夹是否存在，如果不存在，创建文件夹
                File fileParent = file.getParentFile();
    
                if (!fileParent.exists()) {
                    // 文件夹不存在
                    fileParent.mkdirs();// 创建文件夹
                }
    
                // 将图片保存到本地
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
    
                Log.e("cache", "成功写入磁盘缓存 === " + cacheDir);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
    
        /**
         * DiskLruCache会根据我们在调用open()方法时设定的缓存最大值来自动删除多余的缓存。
         * 只有你确定某个key对应的缓存内容已经过期，需要从网络获取最新数据的时候才应该调用remove()方法来移除缓存。
         *
         * @param url
         */
        @Override
        public void remove(String url) {
            String key = null;
    
            try {
                key = MD5Encoder.encode(url);
                mDiskLruCache.remove(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    
    }
    
 
 2、内存缓存
    
    public class MemoryCache implements ImageCache {
   
       private LruCache<String, Bitmap> mMemoryCache;
   
       public MemoryCache() {
           // 初始化LRU缓存
           intiMemoryCache();
       }
   
       private void intiMemoryCache() {
           int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
           int cacheSize = maxMemory / 4;
   
           mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
   
               @Override
               protected int sizeOf(String key, Bitmap value) {
                   // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                   return value.getRowBytes() * value.getHeight() / 1024;
               }
           };
       }
   
       @Override
       public Bitmap get(String url) {
   
           Log.e("cache", "获取的图片来自：内存缓存");
           return mMemoryCache.get(url);
       }
   
       @Override
       public void put(String url, Bitmap bitmap) {
           Log.e("cache", "成功写入内存缓存：" + mMemoryCache.maxSize());
   
           mMemoryCache.put(url, bitmap);
       }
   
       @Override
       public void remove(String url) {
           if (url != null) {
               if (mMemoryCache != null) {
                   Bitmap bm = mMemoryCache.remove(url);
                   if (bm != null)
                       bm.recycle();
               }
           }
       }
    }
   

 3、双重缓存
    
    public class DoubleCache implements ImageCache {
    
        private ImageCache mMemoryCache = new MemoryCache();
        private ImageCache mDiskCache = new DiskCache();
    
        public DoubleCache() {
        }
    
        @Override
        public Bitmap get(String url) {
            Bitmap bitmap = mMemoryCache.get(url);
    
            if (bitmap == null) {
                bitmap = mDiskCache.get(url);
            }
    
            return bitmap;
        }
    
        @Override
        public void put(String url, Bitmap bitmap) {
    
            mMemoryCache.put(url, bitmap);
            mDiskCache.put(url, bitmap);
        }
    
        @Override
        public void remove(String url) {
            mDiskCache.remove(url);
            mMemoryCache.remove(url);
        }
    }
    
    
    /**
     * 存取接口
     * 缓存抽象类
     * 接口隔离原则  缓存的具体实现对ImageLoader隐藏 庞大的接口拆分到具体的接口实现当中
     * <p>
     * Created by Bill on 2017/12/7.
     */
    public interface ImageCache {
    
        Bitmap get(String url);
    
        void put(String url, Bitmap bitmap);
    
        void remove(String url);
    }
   
    
 4、使用
 
    **
     * 主页设置
     */
    public class MainActivity extends AppCompatActivity implements ImageView.OnClickListener {
    
        private ImageLoaderUtil mUtil;
        private ImageView mImageView;
        private String url = "http://img.my.csdn.net/uploads/201407/26/1406383299_1976.jpg";
    
        public static final int DISK = 1;
        public static final int MEMORY = 2;
        public static final int BOTH = 3;
        public static final int NULL = 4;
        private TextView mTvShow;
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
    
            mImageView = findViewById(R.id.iv_image);
            mTvShow = findViewById(R.id.tv_show);
    
            findViewById(R.id.disk).setOnClickListener(this);
            findViewById(R.id.memory).setOnClickListener(this);
            findViewById(R.id.two).setOnClickListener(this);
            findViewById(R.id.reset).setOnClickListener(this);
    
            int type = (int) PreferenceUtil.getObject(PreferenceUtil.getSharedPreference(MainActivity.this), "type", 0);
    
            if (type == 1) {
                mTvShow.setText("展示方式为：磁盘缓存");
                Log.e("cache", "type为：磁盘缓存");
    
                // 获取实例
                mUtil = new ImageLoaderUtil();
                mUtil.setImageCache(new DiskCache(PicApplication.getInstance(), url));
                mUtil.displayImage(url, mImageView);
    
            } else if (type == 2) {
                mTvShow.setText("展示方式为：内存缓存");
                Log.e("cache", "type为：内存缓存");
    
                // 获取实例
                mUtil = new ImageLoaderUtil();
                mUtil.setImageCache(new MemoryCache());
                mUtil.displayImage(url, mImageView);
    
            } else if (type == 3) {
                mTvShow.setText("展示方式为：双重缓存");
                Log.e("cache", "type为：双重缓存");
    
                // 获取实例
                mUtil = new ImageLoaderUtil();
                mUtil.setImageCache(new DoubleCache());
                mUtil.displayImage(url, mImageView);
            } else {
                mTvShow.setText("展示方式为：未设置");
            }
    
           /* // 自定义
            util.setImageCache(new ImageCache() {
                @Override
                public Bitmap get(String url) {
                    return null;
                }
    
                @Override
                public void put(String url, Bitmap bitmap) {
    
                }
            });*/
        }
    
        @Override
        public void onClick(View view) {
    
            // 获取实例
            mUtil = new ImageLoaderUtil();
    
            switch (view.getId()) {
    
                case R.id.disk:  // 磁盘缓存
                    Log.e("cache", "点击了磁盘缓存：");
                    PreferenceUtil.putObject(PreferenceUtil.getSharedPreference(MainActivity.this), "type", DISK);
                    mTvShow.setText("展示方式为：磁盘缓存");
    
                    mUtil.setImageCache(new DiskCache(PicApplication.getInstance(), url));
                    mUtil.displayImage(url, mImageView);
                    break;
    
                case R.id.memory: // 内存缓存
                    Log.e("cache", "点击了内存缓存：");
                    PreferenceUtil.putObject(PreferenceUtil.getSharedPreference(MainActivity.this), "type", MEMORY);
                    mTvShow.setText("展示方式为：内存缓存");
    
                    mUtil.setImageCache(new MemoryCache());
                    mUtil.displayImage(url, mImageView);
                    break;
    
                case R.id.two: // 双缓存
                    Log.e("cache", "点击了双重缓存：");
                    PreferenceUtil.putObject(PreferenceUtil.getSharedPreference(MainActivity.this), "type", BOTH);
                    mTvShow.setText("展示方式为：双重缓存");
    
                    mUtil.setImageCache(new DoubleCache());
                    mUtil.displayImage(url, mImageView);
                    break;
    
                case R.id.reset: // 重置缓存
                    Log.e("cache", "点击了重置缓存：");
                    PreferenceUtil.putObject(PreferenceUtil.getSharedPreference(MainActivity.this), "type", NULL);
                    mTvShow.setText("展示方式为：暂未设置");
    
                    mUtil.setImageCache(null);
                    mUtil.removeCache(url);
                    break;
            }
        }
    }
 
 
 5.实现类
    
        /**
         * 图片加载
         * Created by Bill on 2017/12/7.
         */
        public class ImageLoaderUtil {
        
            private ImageCache mImageCache; // 图片缓存
            private ExecutorService mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); // 线程池，线程数量为CPU的数量
        
            /**
             * 注入缓存类型   依赖于抽象  里氏替换原则
             *
             * @param imageCache
             */
            public void setImageCache(ImageCache imageCache) {
                mImageCache = imageCache;
            }
        
            /**
             * 移除缓存
             *
             * @param url
             */
            public void removeCache(String url) {
                mImageCache.remove(url);
            }
        
            /**
             * 展示图片
             *
             * @param url
             * @param imageView
             */
            public void displayImage(String url, ImageView imageView) {
                Bitmap bitmap = mImageCache.get(url);
        
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
        
                    Log.e("cache", "缓存中获取的图片,显示完成：" + bitmap);
                    return;
                }
        
                loadPic(url, imageView);
            }
        
            /**
             * 线程池下载
             *
             * @param url
             * @param view
             */
            private void loadPic(final String url, final ImageView view) {
                view.setTag(url);
        
                mExecutorService.submit(() -> {
                    Bitmap bitmap = downloadImage(url);
        
                    if (bitmap == null) {
                        return;
                    }
        
                    if (view.getTag().equals(url)) {
                        view.setImageBitmap(bitmap);
        
                        // 显示完成后  放入缓存
                        mImageCache.put(url, bitmap);
        
                        Log.e("cache", "网络下载的图片，显示完成：");
                    }
                });
            }
        
            /**
             * 下载图片
             *
             * @param url
             * @return
             */
            private Bitmap downloadImage(String url) {
                Log.e("cache", "开始从网络下载图片啦 ：");
        
                Bitmap bitmap = null;
        
                try {
                    URL url1 = new URL(url);
        
                    HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                    connection.setDoInput(true);
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
        
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        
                return bitmap;
            }
        }
 
 
 6.工具类
    
    /**
     * Md5加密工具
     */
    public class MD5Encoder {
    
        /**
         * Md5Encoder
         *
         * @param string
         * @return
         * @throws Exception
         */
        public static String encode(String string) throws Exception {
    
            byte[] hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
    
            StringBuilder hex = new StringBuilder(hash.length * 2);
    
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10) {
                    hex.append("0");
                }
                hex.append(Integer.toHexString(b & 0xFF));
            }
            
            return hex.toString();
        }
        
    }
    
    
    // 工具类
    public class loadUtil {

    /**
     * 下载图片转换为流对象
     *
     * @param urlString
     * @param outputStream
     * @return
     */
    public static boolean downloadUrlToStream(String urlString, OutputStream outputStream) {

        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;

        try {
            final URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);

            int b;

            while ((b = in.read()) != -1) {
                out.write(b);
            }

            return true;

        } catch (final IOException e) {
            e.printStackTrace();
        } finally {

            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            try {
                if (out != null) {
                    out.close();
                }

                if (in != null) {
                    in.close();
                }

            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * 获取应用版本号
     *
     * @param context
     * @return
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 1;
    }

    /**
     * 获取缓存路径
     *
     * @param context
     * @param uniqueName
     * @return
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }

        return new File(cachePath + File.separator + uniqueName);
    }
    
    }
    
        