desc "Compile Cimlink_Web and deploy to tomcat"
task :tomcat do
    run ("sudo /home/trustpay/pmtp_tomcat.sh");
end
server 'app4.propensity.co.za', :app, :primary => true
set :user, "trustpay"
set :use_sudo,false
set :application, "Cimlink_Web"
set :repository,  "git@github.com:propensity/cimlink_webclient.git"
set :scm, :git
set :deploy_via,  :rsync_with_remote_cache
set :rsync_options, '--copy-links -az --delete '
set :artifact_command, '(cd Cimlink_Web && ant clean && ant dist )'
set :deploy_to, "/home/#{user}/#{application}"
after "deploy:restart", "tomcat"