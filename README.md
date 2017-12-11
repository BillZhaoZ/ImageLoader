# ImageLoader

图片加载框架（面向对象的原则）
    
    单一职责原则
    开闭原则
    里氏替换原则
    
        
1、磁盘缓存
    
    /**
     * 磁盘缓存
     * Created by Bill on 2017/12/7.
     */
    public class DiskCache implements ImageCache {
    
        private String cacheDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cache/pics";
    
        @Override
        public Bitmap get(String url) {
            // 从本地文件获取图片
    
            try {
                String fileName = MD5Encoder.encode(url);
                File file = new File(cacheDir, fileName);
    
                if (file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
    
                    Log.e("cache", "获取图片：" + bitmap);
    
                    return bitmap;
                }
    
            } catch (Exception e) {
                e.printStackTrace();
            }
    
            return null;
        }
    
        @Override
        public void put(String url, Bitmap bitmap) {
            // 将bitmap写入文件中
    
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
            } catch (Exception e) {
                e.printStackTrace();
            }
    
            Log.e("cache", "成功写入磁盘缓存：" + cacheDir);
        }
    
        @Override
        public void remove(String url) {
    
        }
    }
    
 
 2、内存缓存
    
    /**
     * 内存缓存
     * Created by Bill on 2017/12/7.
     */
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
            return mMemoryCache.get(url);
        }
    
        @Override
        public void put(String url, Bitmap bitmap) {
            mMemoryCache.put(url, bitmap);
    
            Log.e("cache", "成功写入内存缓存：" + mMemoryCache.maxSize());
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
    
    /**
     * 双层缓存
     * Created by Bill on 2017/12/7.
     */
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
    
    
 4、使用
 
    /**
     * 主页设置
     */
    public class MainActivity extends AppCompatActivity implements ImageView.OnClickListener {
    
        private ImageLoaderUtil mUtil;
        private ImageView mImageView;
        private String url = "http://img.my.csdn.net/uploads/201407/26/1406383299_1976.jpg";
    
        public static final int DISK = 1;
        public static final int MEMORY = 2;
        public static final int BOTH = 3;
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
    
            mImageView = findViewById(R.id.iv_image);
    
            findViewById(R.id.disk).setOnClickListener(this);
            findViewById(R.id.memory).setOnClickListener(this);
            findViewById(R.id.two).setOnClickListener(this);
    
            // 获取实例
            mUtil = new ImageLoaderUtil();
    
            int type = (int) PreferenceUtil.getObject(PreferenceUtil.getSharedPreference(MainActivity.this), "type", 0);
    
            if (type != 0) {
                mUtil.displayImage(url, mImageView);
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
    
            switch (view.getId()) {
    
                case R.id.disk:  // 磁盘缓存
                    Log.e("cache", "点击了磁盘缓存：");
    
                    PreferenceUtil.putObject(PreferenceUtil.getSharedPreference(MainActivity.this), "type", DISK);
    
                    mUtil.setImageCache(new DiskCache());
                    mUtil.displayImage(url, mImageView);
                    break;
    
                case R.id.memory: // 内存缓存
                    Log.e("cache", "点击了内存缓存：");
                    PreferenceUtil.putObject(PreferenceUtil.getSharedPreference(MainActivity.this), "type", MEMORY);
    
    
                    mUtil.setImageCache(new MemoryCache());
                    mUtil.displayImage(url, mImageView);
                    break;
    
                case R.id.two: // 双缓存
                    Log.e("cache", "点击了双重缓存：");
                    PreferenceUtil.putObject(PreferenceUtil.getSharedPreference(MainActivity.this), "type", BOTH);
    
                    mUtil.setImageCache(new DoubleCache());
                    mUtil.displayImage(url, mImageView);
                    break;
            }
        }
    }