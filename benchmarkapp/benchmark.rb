require 'net/http'
require 'concurrent'
require 'benchmark'
require 'concurrent'
require 'open3'

api_base_url = 'http://localhost:8080'
num_requests = 100
paths = ['/api/books', '/api/orders', '/rota3']

access_token = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTY5NTYwNzg2OH0.p5nz4AAqqEG6U6VVYzPsVq8LC3beZkExir7K1s-xtNWCPB80pWj_qoQCmCZ0D5E0w7V5hO-vlOt1hO4k-TwTdA'
output_file = 'tempos.txt'
output = File.open(output_file, 'w')

def fazer_requisicao(api_base_url, path, access_token, output)
  path = paths.sample
  result, _ = Open3.capture2e("curl -X 'get' '#{api_base_url}#{path}' -H 'accept: */*' -H 'Authorization: Bearer #{access_token}'")
  response_time = result.match(/time_total:\s+([\d.]+) ms/)&.captures&.first
  output.puts("#{response_time}")
end

start_time = Time.now

thread_pool = Concurrent::ThreadPoolExecutor.new(max_threads: num_requests)

num_requests.times do
  thread_pool.post do
    fazer_requisicao(api_base_url, path, access_token, output)
  end
end

thread_pool.shutdown
thread_pool.wait_for_termination

end_time = Time.now

output.close

total_time = end_time - start_time
puts "Total time: #{total_time} seconds"
