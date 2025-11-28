using Common.Security;
using Common.Utils;
using Configs;
using DB.Model;
using NHibernate;

var builder = WebApplication.CreateBuilder(args);

// DB 세팅
var cfg = DBHelper.ConfigureNHibernate();
ISessionFactory sessionFactory = cfg.BuildSessionFactory();

builder.Services.AddEncryption();
builder.Services.AddSingleton(sessionFactory);
builder.Services.AddScoped(factory => 
    factory.GetService<ISessionFactory>()!.OpenSession());

// Add services to the container.
builder.Services.AddControllersWithViews();

var app = builder.Build();

// 암호화 세팅
EncryptUtils.Initialize(
    app.Services.GetRequiredService<IAesStringEncryptor>()
);

// Configure the HTTP request pipeline.
if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Home/Error");
    // The default HSTS value is 30 days. You may want to change this for production scenarios, see https://aka.ms/aspnetcore-hsts.
    app.UseHsts();
}

app.UseHttpsRedirection();
app.UseRouting();

app.UseAuthorization();

app.MapStaticAssets();

app.MapControllerRoute(
        name: "default",
        pattern: "{controller=Home}/{action=Index}/{id?}")
    .WithStaticAssets();


app.Run();