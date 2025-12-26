create table blog_articles
(
    id         varchar(255)             not null
        constraint articles_pkey
            primary key,
    title      varchar(200)             not null,
    excerpt    text,
    content    text                     not null,
    author     varchar(100)             not null,
    date       timestamp with time zone not null,
    category   varchar(50)              not null,
    image_url  varchar(500),
    tags       jsonb,
    views      integer   default 0      not null,
    created_at timestamp default CURRENT_TIMESTAMP,
    updated_at timestamp default CURRENT_TIMESTAMP
);

alter table blog_articles
    owner to postgres;

create index idx_articles_category
    on blog_articles (category);

create index idx_articles_author
    on blog_articles (author);

create index idx_articles_views
    on blog_articles (views desc);

create index idx_articles_tags
    on blog_articles using gin (tags);

create index idx_articles_date
    on blog_articles (date desc);

-- 博客设置相关表 --
CREATE TABLE blog_settings (
                               id VARCHAR(36) PRIMARY KEY NOT NULL ,
                               key VARCHAR(100) NOT NULL UNIQUE,
                               value TEXT,
                               description VARCHAR(255),
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 添加注释
COMMENT ON TABLE blog_settings IS '系统设置表';
COMMENT ON COLUMN blog_settings.id IS '主键ID(UUID)';
COMMENT ON COLUMN blog_settings.key IS '设置键名';
COMMENT ON COLUMN blog_settings.value IS '设置值';
COMMENT ON COLUMN blog_settings.description IS '设置描述';

-- 创建索引
CREATE INDEX idx_blog_settings_key ON blog_settings(key);

-- 创建更新时间触发器
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_blog_settings_updated_at BEFORE UPDATE ON blog_settings
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TABLE blog_article_categories (
                                         id VARCHAR(50) PRIMARY KEY NOT NULL ,
                                         code VARCHAR(50) NOT NULL UNIQUE,
                                         name_zh VARCHAR(100) NOT NULL,
                                         name_en VARCHAR(100) NOT NULL,
                                         remark TEXT,
                                         sort_order INTEGER DEFAULT 0,
                                         article_count INTEGER DEFAULT 0,
                                         is_active BOOLEAN DEFAULT TRUE,
                                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 添加注释
COMMENT ON TABLE blog_article_categories IS '文章分类表';
COMMENT ON COLUMN blog_article_categories.id IS '主键ID(UUID)';
COMMENT ON COLUMN blog_article_categories.code IS '分类代码';
COMMENT ON COLUMN blog_article_categories.name_zh IS '中文名称';
COMMENT ON COLUMN blog_article_categories.name_en IS '英文名称';
COMMENT ON COLUMN blog_article_categories.remark IS '备注说明';
COMMENT ON COLUMN blog_article_categories.sort_order IS '排序号';
COMMENT ON COLUMN blog_article_categories.article_count IS '文章数量';
COMMENT ON COLUMN blog_article_categories.is_active IS '是否启用';

-- 创建索引
CREATE INDEX idx_blog_article_categories_code ON blog_article_categories(code);
CREATE INDEX idx_blog_article_categories_active ON blog_article_categories(is_active);
CREATE INDEX idx_blog_article_categories_sort ON blog_article_categories(sort_order);

-- 创建更新时间触发器
CREATE TRIGGER update_blog_article_categories_updated_at BEFORE UPDATE ON blog_article_categories
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TABLE blog_music_tracks (
                                   id VARCHAR(36) PRIMARY KEY NOT NULL ,
                                   name VARCHAR(200) NOT NULL,
                                   author VARCHAR(100),
                                   url VARCHAR(500) NOT NULL,
                                   duration INTEGER,
                                   sort_order INTEGER DEFAULT 0,
                                   play_count INTEGER DEFAULT 0,
                                   is_active BOOLEAN DEFAULT TRUE,
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 添加注释
COMMENT ON TABLE blog_music_tracks IS '音乐曲目表';
COMMENT ON COLUMN blog_music_tracks.id IS '主键ID(UUID)';
COMMENT ON COLUMN blog_music_tracks.name IS '歌曲名称';
COMMENT ON COLUMN blog_music_tracks.author IS '艺术家';
COMMENT ON COLUMN blog_music_tracks.url IS '音频URL';
COMMENT ON COLUMN blog_music_tracks.duration IS '时长(秒)';
COMMENT ON COLUMN blog_music_tracks.sort_order IS '排序号';
COMMENT ON COLUMN blog_music_tracks.play_count IS '播放次数';
COMMENT ON COLUMN blog_music_tracks.is_active IS '是否启用';

-- 创建索引
CREATE INDEX idx_blog_music_tracks_active ON blog_music_tracks(is_active);
CREATE INDEX idx_blog_music_tracks_sort ON blog_music_tracks(sort_order);

-- 创建更新时间触发器
CREATE TRIGGER update_blog_music_tracks_updated_at BEFORE UPDATE ON blog_music_tracks
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();