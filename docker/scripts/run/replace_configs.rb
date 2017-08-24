files_to_replace = ['/usr/local/tomcat/extra_properties/default.properties',
                    '/usr/local/tomcat/extra_properties/app.properties']

variables = { POSTGRES_HOST: ENV["POSTGRES_HOST"],
              POSTGRES_DB: ENV["POSTGRES_DB"],
              POSTGRES_USER: ENV["POSTGRES_USER"],
              POSTGRES_PASSWORD: ENV["POSTGRES_PASSWORD"],
              APP_URL: ENV["APP_URL"]
            }

files_to_replace.each do |file|
  content_to_replace_with = File.read(file)
  File.write(file, content_to_replace_with % variables)
end

